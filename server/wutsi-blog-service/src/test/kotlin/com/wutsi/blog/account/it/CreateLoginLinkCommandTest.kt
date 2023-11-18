package com.wutsi.blog.account.it

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.CreateLoginLinkResponse
import com.wutsi.blog.account.dto.LoginLinkCreatedEventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.stream.EventStream
import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CreateLoginLinkCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    protected lateinit var eventStream: EventStream

    @Value("\${spring.mail.port}")
    private lateinit var port: String

    private lateinit var smtp: GreenMail

    private val command = CreateLoginLinkCommand(
        referer = "foo",
        redirectUrl = "https://www.google.ca",
        storyId = 111L,
        email = "herve.tchepannou@gmail.com",
        language = "en",
    )

    @BeforeEach
    fun setUp() {
        smtp = GreenMail(ServerSetup.SMTP.port(port.toInt()))
        smtp.setUser("wutsi", "secret")
        smtp.start()
    }

    @AfterEach
    fun tearDown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
    }

    @Test
    @Sql(value = ["/db/clean.sql"])
    fun create() {
        val result = rest.postForEntity("/v1/auth/commands/create-link", command, CreateLoginLinkResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.OK)

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = "-",
            type = EventType.LOGIN_LINK_CREATED_EVENT,
        )
        assertTrue(events.isNotEmpty())
        val payload = events[0].payload as LoginLinkCreatedEventPayload
        assertEquals(command.referer, payload.referer)
        assertEquals(command.redirectUrl, payload.redirectUrl)
        assertEquals(command.storyId, payload.storyId)
        assertEquals(command.email, payload.email)
        assertEquals(command.language, payload.language)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())

        println("------------------------------")
        print(messages[0].content.toString())

        assertTrue(deliveredTo(command.email, messages))
    }

    @Test
    @Sql(value = ["/db/clean.sql"])
    fun createAsync() {
        eventStream.enqueue(EventType.CREATE_LOGIN_LINK_COMMAND, command)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = "-",
            type = EventType.LOGIN_LINK_CREATED_EVENT,
        )
        assertTrue(events.isNotEmpty())
        val payload = events[0].payload as LoginLinkCreatedEventPayload
        assertEquals(command.referer, payload.referer)
        assertEquals(command.redirectUrl, payload.redirectUrl)
        assertEquals(command.storyId, payload.storyId)
        assertEquals(command.email, payload.email)
        assertEquals(command.language, payload.language)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())

        println("------------------------------")
        print(messages[0].content.toString())

        assertTrue(deliveredTo(command.email, messages))
    }

    fun deliveredTo(email: String, messages: Array<MimeMessage>): Boolean =
        messages.find { message ->
            message.getRecipients(Message.RecipientType.TO).find {
                it.toString().contains(email)
            } != null
        } != null
}
