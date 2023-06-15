package com.wutsi.blog.story.it

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.wutsi.blog.story.job.StoryEmailNotificationJob
import jakarta.mail.Message
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/StoryEmailNotificationJob.sql"])
class StoryEmailNotificationJobTest {
    @Autowired
    private lateinit var job: StoryEmailNotificationJob

    @Value("\${spring.mail.port}")
    private lateinit var port: String

    private lateinit var smtp: GreenMail

    @BeforeEach
    fun setUp() {
        smtp = GreenMail(ServerSetup.SMTP.port(port.toInt()))
        smtp.setUser("wutsi", "secret")
        smtp.start()
    }

    @AfterEach
    fun tearDoown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
    }

    @Test
    fun run() {
        job.run()
        Thread.sleep(30000)

        val messages = smtp.receivedMessages
        assertEquals(1, messages.size)

        assertEquals("text/html;charset=UTF-8", messages[0].contentType)
        assertEquals("Hello world", messages[0].subject)
        assertEquals("Ray Sponsible <no-reply@wutsi.com>", messages[0].from[0].toString())
        assertEquals(
            "John Smith <john.smith@gmail.com>",
            messages[0].getRecipients(Message.RecipientType.TO)[0].toString(),
        )
    }
}
