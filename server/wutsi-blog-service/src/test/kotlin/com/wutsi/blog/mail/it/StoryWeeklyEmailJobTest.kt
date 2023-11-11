package com.wutsi.blog.mail.it

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.backend.PersonalizeBackend
import com.wutsi.blog.mail.job.StoryWeeklyEmailJob
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.event.store.EventStore
import com.wutsi.ml.personalize.dto.SortStoryResponse
import com.wutsi.ml.personalize.dto.Story
import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/mail/StoryWeeklyEmailJobTest.sql"])
class StoryWeeklyEmailJobTest {
    @Autowired
    private lateinit var job: StoryWeeklyEmailJob

    @Value("\${spring.mail.port}")
    private lateinit var port: String

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    protected lateinit var storyDao: StoryRepository

    @MockBean
    protected lateinit var clock: Clock

    @MockBean
    private lateinit var personalizeBackend: PersonalizeBackend

    private lateinit var smtp: GreenMail

    @BeforeEach
    fun setUp() {
        smtp = GreenMail(ServerSetup.SMTP.port(port.toInt()))
        smtp.setUser("wutsi", "secret")
        smtp.start()

        val date = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-20")
        doReturn(date.time).whenever(clock).millis()

        doReturn(
            SortStoryResponse(
                listOf(
                    Story(id = 10, score = 1.0),
                    Story(id = 11, score = 1.0),
                    Story(id = 12),
                    Story(id = 13),
                    Story(id = 14),
                    Story(id = 20),
                    Story(id = 30)
                )
            )
        ).whenever(personalizeBackend).sort(any())
    }

    @AfterEach
    fun tearDown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
    }

    @Test
    fun run() {
        job.run()
        Thread.sleep(15000)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        println("------------------------------")
        print(messages[0].content.toString())

        assertTrue(deliveredTo("tchbansi@hotmail.com", messages))
        assertFalse(deliveredTo("user-not-whitelisted@gmail.com", messages))
        assertFalse(deliveredTo("blacklisted@gmail.com", messages))

//        val events = eventStore.events(
//            streamId = StreamId.STORY,
//            entityId = "10",
//            type = EventType.STORY_DAILY_EMAIL_SENT_EVENT,
//        )
//        assertTrue(events.isNotEmpty())

//        val story = storyDao.findById(10L).get()
//        assertEquals(2L, story.recipientCount)
    }

    fun deliveredTo(email: String, messages: Array<MimeMessage>): Boolean =
        messages.find { message ->
            message.getRecipients(Message.RecipientType.TO).find {
                it.toString().contains(email)
            } != null
        } != null
}
