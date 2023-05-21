package com.wutsi.blog.channel

import com.wutsi.blog.EventHandler
import com.wutsi.blog.channel.dao.ChannelRepository
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.blog.client.channel.CreateChannelResponse
import com.wutsi.blog.client.channel.GetChannelResponse
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/ChannelController.sql"])
class ChannelControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: ChannelRepository

    @Autowired
    private lateinit var eventHandler: EventHandler

    @BeforeEach
    fun setUp() {
        eventHandler.init()
    }

    @Test
    fun get() {
        val result = rest.getForEntity("/v1/channels/10", GetChannelResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val channel = result.body!!.channel

        assertEquals("FB", channel.name)
        assertEquals("1111", channel.providerUserId)
        assertEquals("https://img.com/fb-000010.png", channel.pictureUrl)
        assertEquals(ChannelType.facebook, channel.type)
        assertNotNull(channel.creationDateTime)
        assertNotNull(channel.modificationDateTime)
    }

    @Test
    fun getNotFound() {
        val result = rest.getForEntity("/v1/channels/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        val error = result.body!!.error

        assertEquals("channel_not_found", error.code)
    }

    @Test
    fun search() {
        val result = rest.getForEntity("/v1/channels?userId=1", SearchChannelResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val channels = result.body!!.channels

        assertEquals(3, channels.size)

        assertEquals(10, channels[0].id)
        assertEquals("1111", channels[0].providerUserId)
        assertEquals("FB", channels[0].name)
        assertEquals("https://img.com/fb-000010.png", channels[0].pictureUrl)
        assertEquals(ChannelType.facebook, channels[0].type)
        assertNotNull(channels[0].creationDateTime)
        assertNotNull(channels[0].modificationDateTime)

        assertEquals(11, channels[1].id)
        assertEquals("2222", channels[1].providerUserId)
        assertEquals("TW", channels[1].name)
        assertEquals("https://img.com/tw-000011.png", channels[1].pictureUrl)
        assertEquals(ChannelType.twitter, channels[1].type)
        assertNotNull(channels[1].creationDateTime)
        assertNotNull(channels[1].modificationDateTime)

        assertEquals(12, channels[2].id)
        assertEquals("3333", channels[2].providerUserId)
        assertEquals("LK", channels[2].name)
        assertNull(channels[2].pictureUrl)
        assertEquals(ChannelType.linkedin, channels[2].type)
        assertNotNull(channels[2].creationDateTime)
        assertNotNull(channels[2].modificationDateTime)
    }

    @Test
    fun create() {
        val request = CreateChannelRequest(
            name = "test-create",
            providerUserId = "1111111",
            pictureUrl = "https://pic.com/test-create.png",
            accessToken = "test-create-token",
            accessTokenSecret = "test-create-token-secret",
            userId = 2L,
            type = ChannelType.linkedin,
        )
        val result = rest.postForEntity("/v1/channels", request, CreateChannelResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val channelId = result.body!!.channelId

        val channel = dao.findById(channelId).get()
        assertEquals(request.providerUserId, channel.providerUserId)
        assertEquals(request.name, channel.name)
        assertEquals(request.pictureUrl, channel.pictureUrl)
        assertEquals(request.type, channel.type)
        assertEquals(request.accessToken, channel.accessToken)
        assertEquals(request.accessTokenSecret, channel.accessTokenSecret)
        assertNotNull(channel.creationDateTime)
        assertNotNull(channel.modificationDateTime)
    }

    @Test
    fun createFireEvent() {
        val request = CreateChannelRequest(
            name = "test-create",
            providerUserId = "1111111",
            pictureUrl = "https://pic.com/test-create.png",
            accessToken = "test-create-token",
            accessTokenSecret = "test-create-token-secret",
            userId = 3L,
            type = ChannelType.twitter,
        )
        val result = rest.postForEntity("/v1/channels", request, CreateChannelResponse::class.java)

        assertEquals(result.body?.channelId, eventHandler.channelBoundedEvent?.channelId)
    }

    @Test
    fun delete() {
        rest.delete("/v1/channels/20")

        val channel = dao.findById(20L)
        assertFalse(channel.isPresent)
    }

    @Test
    fun deleteFireEvent() {
        rest.delete("/v1/channels/21")

        assertEquals(21L, eventHandler.channelUnboundedEvent?.channelId)
        assertEquals(2L, eventHandler.channelUnboundedEvent?.userId)
        assertEquals(ChannelType.twitter, eventHandler.channelUnboundedEvent?.channelType)
    }
}
