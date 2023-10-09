package com.wutsi.blog.endorsement.it

import com.wutsi.blog.endorsement.dto.SearchEndorsementRequest
import com.wutsi.blog.endorsement.dto.SearchEndorsementResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/endorsement/SearchEndorsementQuery.sql"])
internal class SearchEndorsementQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun byUserIds() {
        // WHEN
        val request = SearchEndorsementRequest(
            userIds = listOf(1, 3),
        )
        val response =
            rest.postForEntity("/v1/endorsements/queries/search", request, SearchEndorsementResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val endorsements = response.body!!.endorsements.sortedBy { it.userId }

        assertEquals(3, endorsements.size)

        assertEquals(1L, endorsements[0].userId)
        assertEquals(2L, endorsements[0].endorserId)

        assertEquals(1L, endorsements[1].userId)
        assertEquals(3L, endorsements[1].endorserId)

        assertEquals(3L, endorsements[2].userId)
        assertEquals(2L, endorsements[2].endorserId)
    }

    @Test
    fun byEndorserIds() {
        // WHEN
        val request = SearchEndorsementRequest(
            endorserId = 2L,
        )
        val response =
            rest.postForEntity("/v1/endorsements/queries/search", request, SearchEndorsementResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val endorsements = response.body!!.endorsements.sortedBy { it.userId }

        assertEquals(2, endorsements.size)

        assertEquals(1L, endorsements[0].userId)
        assertEquals(2L, endorsements[0].endorserId)

        assertEquals(3L, endorsements[1].userId)
        assertEquals(2L, endorsements[1].endorserId)
    }
}
