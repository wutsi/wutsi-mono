package com.wutsi.blog.user.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.dto.RecommendUserResponse
import com.wutsi.blog.user.service.UserRecommendationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RecommendUserQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var service: UserRecommendationService

    @Test
    fun recommend() {
        // GIVEN
        val result = listOf(11L, 13L, 14L)
        doReturn(result).whenever(service).recommend(any())

        // WHEN
        val request = RecommendUserRequest(
            readerId = 1L,
            deviceId = "103920932",
            limit = 3,
        )
        val response = rest.postForEntity(
            "/v1/users/queries/recommend",
            request,
            RecommendUserResponse::class.java,
        )

        kotlin.test.assertEquals(HttpStatus.OK, response.statusCode)

        val userIds = response.body!!.userIds
        kotlin.test.assertEquals(listOf(11L, 13L, 14L), userIds)

        verify(service).recommend(request)
    }
}
