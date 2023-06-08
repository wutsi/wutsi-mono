package com.wutsi.blog.account

import com.wutsi.blog.EventHandler
import com.wutsi.blog.account.dao.UserRepository
import com.wutsi.blog.client.user.CountUserResponse
import com.wutsi.blog.client.user.GetUserResponse
import com.wutsi.blog.client.user.SearchUserResponse
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.client.user.UpdateUserAttributeResponse
import com.wutsi.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/UserController.sql"])
class UserControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var eventHandler: EventHandler

    @BeforeEach
    fun setUp() {
        eventHandler.init()
    }

    @Test
    fun get() {
        val result = rest.getForEntity("/v1/users/1", GetUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val user = result.body!!.user
        assertEquals(1L, user.id)
        assertEquals("ray.sponsible", user.name)
        assertEquals("Angel investor", user.biography)
        assertEquals("Ray Sponsible", user.fullName)
        assertEquals("https://picture.com/ray.sponsible", user.pictureUrl)
        assertEquals("ray.sponsible@gmail.com", user.email)
        assertEquals("https://me.com/ray.sponsible", user.websiteUrl)
        assertEquals(5L, user.loginCount)
        assertTrue(user.superUser)
        assertTrue(user.blog)
        assertEquals("ray.sponsible", user.telegramId)
        assertEquals("23799505555", user.whatsappId)
        assertEquals("23799505555", user.whatsappId)
        assertEquals(fmt.format(Date()), fmt.format(user.lastPublicationDateTime))
    }

    @Test
    fun getNotFound() {
        val result = rest.getForEntity("/v1/users/999", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("user_not_found", result.body!!.error.code)
    }

    @Test
    fun getByName() {
        val result = rest.getForEntity("/v1/users/@/ray.sponsible", GetUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val user = result.body!!.user
        assertEquals(1L, user.id)
        assertEquals("ray.sponsible", user.name)
        assertEquals("Angel investor", user.biography)
        assertEquals("Ray Sponsible", user.fullName)
        assertEquals("https://picture.com/ray.sponsible", user.pictureUrl)
        assertEquals("ray.sponsible@gmail.com", user.email)
        assertEquals("https://me.com/ray.sponsible", user.websiteUrl)
        assertEquals(5L, user.loginCount)
    }

    @Test
    fun getByNameNotFound() {
        val result = rest.getForEntity("/v1/users/@/xxxx", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("user_not_found", result.body!!.error.code)
    }

    @Test
    fun search() {
        val result = rest.getForEntity("/v1/users?userId=1&userId=2&userId=4", SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(3, users.size)

        assertEquals(1L, users[0].id)
        assertEquals("ray.sponsible", users[0].name)
        assertEquals(true, users[0].blog)
        assertEquals("Ray Sponsible", users[0].fullName)
        assertEquals("https://picture.com/ray.sponsible", users[0].pictureUrl)

        assertEquals(2L, users[1].id)
        assertEquals("jane.doe", users[1].name)
        assertEquals(false, users[1].blog)
        assertEquals("Jane Doe", users[1].fullName)
        assertEquals("https://picture.com/jane.doe", users[1].pictureUrl)

        assertEquals(4L, users[2].id)
        assertEquals("logout", users[2].name)
        assertEquals(false, users[2].blog)
        assertEquals("Logout", users[2].fullName)
        assertNull(users[2].pictureUrl)
    }

    @Test
    fun searchUserBlog() {
        val result = rest.getForEntity("/v1/users?blog=true", SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        var users = result.body!!.users
        assertEquals(1, users.size)

        assertEquals(1L, users[0].id)
        assertEquals("ray.sponsible", users[0].name)
        assertEquals(true, users[0].blog)
        assertEquals("Ray Sponsible", users[0].fullName)
        assertEquals("https://picture.com/ray.sponsible", users[0].pictureUrl)
    }

    @Test
    fun searchSortByStoryCount() {
        val result = rest.getForEntity("/v1/users?sortBy=stories&sortOrder=descending", SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        var users = result.body!!.users
        assertEquals(15, users.size)

        assertEquals(40L, users[0].id)
    }

    @Test
    fun count() {
        val result = rest.getForEntity("/v1/users/count", CountUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(15, result.body!!.total)
    }

    @Test
    fun rename() {
        val name = "new-name" + System.currentTimeMillis()
        testUpdateAttribute(10, "name", name)
    }

    @Test
    fun renameInvalidId() {
        val name = "new-name" + System.currentTimeMillis()
        val request = UpdateUserAttributeRequest(
            name = "name",
            value = name
        )

        val result = rest.postForEntity("/v1/users/9999/attributes", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals("user_not_found", result.body!!.error.code)
    }

    @Test
    fun renameDuplicateName() {
        testUpdateAttributeWithError(20, "name", "duplicate.name", HttpStatus.CONFLICT, "duplicate_name")
    }

    @Test
    fun updateEmail() {
        val email = "new-email" + System.currentTimeMillis() + "@gmail.com"
        testUpdateAttribute(20, "email", email)
    }

    @Test
    fun updateDuplicateEmail() {
        testUpdateAttributeWithError(20, "email", "duplicate.email@gmail.com", HttpStatus.CONFLICT, "duplicate_email")
    }

    @Test
    fun updateFullname() {
        testUpdateAttribute(30, "full_name", "Joe Moll")
    }

    @Test
    fun updatePicture() {
        testUpdateAttribute(40, "picture_url", "http://www.google.com/picture/1.png")
    }

    @Test
    fun updateWebsiteUrl() {
        testUpdateAttribute(40, "website_url", "http://www.google.com/ray.sponsible")
    }

    @Test
    fun updateBiography() {
        testUpdateAttribute(40, "biography", "Yo man")
    }

    @Test
    fun updateFacebookId() {
        testUpdateAttribute(40, "facebook_id", "ray.sponsible")
    }

    @Test
    fun updateTwitterId() {
        testUpdateAttribute(40, "twitter_id", "ray.sponsible")
    }

    @Test
    fun updateLanguage() {
        testUpdateAttribute(40, "language", "fr")
    }

    @Test
    fun testTelegram() {
        testUpdateAttribute(20, "telegram_id", "foo.bar")
    }

    @Test
    fun testWhatsapp() {
        testUpdateAttribute(20, "whatsapp_id", "foo.bar")
    }

    @Test
    fun updateInvalidAttribute() {
        testUpdateAttributeWithError(
            20,
            "unknwon-attribute",
            "duplicate.email@gmail.com",
            HttpStatus.CONFLICT,
            "invalid_attribute"
        )
    }

    private fun testUpdateAttribute(userId: Long, name: String, value: String?) {
        val request = UpdateUserAttributeRequest(
            name = name,
            value = value
        )

        val result =
            rest.postForEntity("/v1/users/$userId/attributes", request, UpdateUserAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(userId, result.body?.userId)

        val user = userDao.findById(userId).get()
        if ("language" == name) {
            assertEquals(value, user.language)
        } else if ("twitter_id" == name) {
            assertEquals(value, user.twitterId)
        } else if ("facebook_id" == name) {
            assertEquals(value, user.facebookId)
        } else if ("biography" == name) {
            assertEquals(value, user.biography)
        } else if ("website_url" == name) {
            assertEquals(value, user.websiteUrl)
        } else if ("picture_url" == name) {
            assertEquals(value, user.pictureUrl)
        } else if ("full_name" == name) {
            assertEquals(value, user.fullName)
        } else if ("email" == name) {
            assertEquals(value, user.email)
        } else if ("name" == name) {
            assertEquals(value, user.name)
        }

        assertNotNull(eventHandler.updateUserEvent)
        assertEquals(value, eventHandler.updateUserEvent?.value)
        assertEquals(name, eventHandler.updateUserEvent?.name)
        assertEquals(userId, eventHandler.updateUserEvent?.userId)
    }

    private fun testUpdateAttributeWithError(
        userId: Long,
        name: String,
        value: String,
        status: HttpStatus,
        errorCode: String
    ) {
        val request = UpdateUserAttributeRequest(
            name = name,
            value = value
        )
        val result = rest.postForEntity("/v1/users/$userId/attributes", request, ErrorResponse::class.java)

        assertEquals(status, result.statusCode)
        assertEquals(errorCode, result.body?.error?.code)

        assertNull(eventHandler.updateUserEvent)
    }
}
