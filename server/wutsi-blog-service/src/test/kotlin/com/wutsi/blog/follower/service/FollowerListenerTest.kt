package com.wutsi.blog.follower.service

import com.wutsi.blog.account.dao.UserRepository
import com.wutsi.blog.client.event.FollowEvent
import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.client.event.UnfollowEvent
import com.wutsi.blog.client.event.UpdateUserEvent
import com.wutsi.blog.follower.dao.FollowerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/FollowerListener.sql"])
class FollowerListenerTest {
    @Autowired
    lateinit var listener: FollowerListener

    @Autowired
    lateinit var userDao: UserRepository

    @Autowired
    lateinit var followerDao: FollowerRepository

    @Test
    fun onFollow() {
        listener.onFollow(FollowEvent(1, 4))

        Thread.sleep(1000)
        val user = userDao.findById(1L).get()
        assertEquals(3, user.followerCount)
    }

    @Test
    fun onUnFollow() {
        listener.onUnFollow(UnfollowEvent(2, 4))

        Thread.sleep(1000)
        val user = userDao.findById(2L).get()
        assertEquals(2, user.followerCount)
    }

    @Test
    fun onUserUpdated() {
        listener.onUserUpdated(UpdateUserEvent(1, "blog", "true"))

        Thread.sleep(1000)

        val user = userDao.findById(40).get()
        assertEquals(1, user.followerCount)

        val followers = followerDao.findByUserId(user.id!!)
        assertEquals(1, followers.size)
        assertEquals(1L, followers[0].followerUserId)
    }

    @Test
    fun onStoryPublished() {
        listener.onPublish(PublishEvent(1))

        Thread.sleep(1000)

        val user = userDao.findById(40).get()
        assertEquals(1, user.followerCount)

        val followers = followerDao.findByUserId(user.id!!)
        assertEquals(1, followers.size)
        assertEquals(1L, followers[0].followerUserId)
    }
}
