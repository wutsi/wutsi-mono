package com.wutsi.blog.story.it

import com.wutsi.blog.story.job.StoryEmailNotificationJob
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/StoryEmailNotificationJob.sql"])
class StoryEmailNotificationJobTest {
    @Autowired
    private lateinit var job: StoryEmailNotificationJob

//    @MockBean
//    private lateinit var eventStream: EventStream

    @Test
    fun run() {
        job.run()
        Thread.sleep(30000)

//        val payload = argumentCaptor<SendStoryEmailNotificationCommand>()
//        verify(eventStream).enqueue(eq(EventType.STORY_EMAIL_NOTIFICATION_SENT_EVENT), payload.capture())
//        assertEquals(1L, payload.firstValue.storyId)
//        assertEquals(2L, payload.firstValue.recipientId)
    }
}
