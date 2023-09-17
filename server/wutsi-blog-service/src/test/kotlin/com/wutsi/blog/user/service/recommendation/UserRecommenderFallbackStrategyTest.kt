package com.wutsi.blog.user.service.recommendation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SortOrder
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import com.wutsi.blog.user.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRecommenderFallbackStrategyTest {
    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var strategy: UserRecommenderFallbackStrategy

    @Test
    fun recommend() {
        // GIVEN
        doReturn(
            listOf(
                UserEntity(id = 20),
                UserEntity(id = 30),
                UserEntity(id = 40),
            ),
        ).whenever(userService).search(any())

        // WHEN
        val result = strategy.recommend(
            RecommendUserRequest(
                readerId = 10L,
                deviceId = "1111",
                limit = 3,
            ),
        )

        // THEN
        assertEquals(listOf(20L, 30L, 40L), result)

        val req = argumentCaptor<SearchUserRequest>()
        verify(userService).search(req.capture())

        assertEquals(listOf(10L), req.firstValue.excludeUserIds)
        assertEquals(true, req.firstValue.blog)
        assertEquals(true, req.firstValue.withPublishedStories)
        assertEquals(true, req.firstValue.active)
        assertEquals(3, req.firstValue.limit)
        assertEquals(UserSortStrategy.POPULARITY, req.firstValue.sortBy)
        assertEquals(SortOrder.DESCENDING, req.firstValue.sortOrder)
    }
}
