package com.wutsi.blog.user.endpoint

import com.wutsi.blog.SortOrder
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSortStrategy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/user/SearchUserQuery.sql"])
class SearchUserQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        val request = SearchUserRequest(
            userIds = listOf(1L, 2L, 4L),
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
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
        val request = SearchUserRequest(
            blog = true,
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(1, users.size)

        assertEquals(1L, users[0].id)
        assertEquals("ray.sponsible", users[0].name)
        assertEquals(true, users[0].blog)
        assertEquals("Ray Sponsible", users[0].fullName)
        assertEquals("https://picture.com/ray.sponsible", users[0].pictureUrl)
    }

    @Test
    fun searchSortByStoryCount() {
        val request = SearchUserRequest(
            sortBy = UserSortStrategy.STORY_COUNT,
            sortOrder = SortOrder.DESCENDING,
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(15, users.size)

        assertEquals(40L, users[0].id)
    }

    @Test
    fun searchActive() {
        val request = SearchUserRequest(
            active = false,
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(2, users.size)

        assertEquals(1L, users[0].id)
        assertEquals(2L, users[1].id)
    }

    @Test
    fun excludeUserIds() {
        val request = SearchUserRequest(
            userIds = listOf(1L, 2L, 4L),
            excludeUserIds = listOf(4L),
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(2, users.size)

        assertEquals(1L, users[0].id)

        assertEquals(2L, users[1].id)
    }

    @Test
    fun byCountry() {
        val request = SearchUserRequest(
            country = "CM",
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(3, users.size)

        assertEquals(1L, users[0].id)
        assertEquals(6L, users[1].id)
        assertEquals(10L, users[2].id)
    }

    @Test
    fun wpp() {
        val request = SearchUserRequest(
            wpp = true,
        )
        val result = rest.postForEntity("/v1/users/queries/search", request, SearchUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users
        assertEquals(2, users.size)

        assertEquals(10L, users[0].id)
        assertTrue(users[0].wpp)

        assertEquals(11L, users[1].id)
        assertTrue(users[1].wpp)
    }
}
