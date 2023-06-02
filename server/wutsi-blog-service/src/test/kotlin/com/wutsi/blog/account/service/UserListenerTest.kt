package com.wutsi.blog.account.service

import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.dao.UserEntityRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/UserListener.sql"])
class UserListenerTest {
    @Autowired
    private lateinit var listener: UserListener

    @Autowired
    private lateinit var userDao: UserEntityRepository

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Test
    fun onPublish() {
        listener.onPublish(
            PublishEvent(
                storyId = 1,
            ),
        )

        Thread.sleep(1000)

        val user = userDao.findById(1L).get()
        val story = storyDao.findById(1L).get()

        assertTrue(user.blog)
        assertEquals(2, user.storyCount)
        assertEquals(story.publishedDateTime, user.lastPublicationDateTime)
    }
}
