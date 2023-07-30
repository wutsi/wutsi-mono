package com.wutsi.ml.embedding.endpoint

import com.wutsi.blog.ml.dto.SearchSimilarityRequest
import com.wutsi.blog.ml.dto.SearchSimilarityResponse
import com.wutsi.ml.embedding.service.TfIdfConfig
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SearchSimilarStoriesQueryTest {
    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var rest: TestRestTemplate

    @BeforeEach
    fun setUp() {
        storage.store(
            path = TfIdfConfig.NN_INDEX_PATH,
            content = ByteArrayInputStream(
                """
                    100,200,300,400
                    1.0,0.1,0.2,0.0
                    0.1,1.0,0.9,0.1
                    0.2,0.9,1.0,0.3
                    0.0,0.1,0.3,1.0
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )
    }

    @Test
    fun search() {
        // WHEN
        val request = SearchSimilarityRequest(
            id = 300,
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val similarities = response.body!!.similarities
        assertEquals(3, similarities.size)

        assertEquals(200L, similarities[0].id)
        assertEquals(0.9, similarities[0].score)

        assertEquals(400L, similarities[1].id)
        assertEquals(0.3, similarities[1].score)

        assertEquals(100L, similarities[2].id)
        assertEquals(0.2, similarities[2].score)
    }

    @Test
    fun badId() {
        // WHEN
        val request = SearchSimilarityRequest(
            id = 99999,
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val similarities = response.body!!.similarities
        assertEquals(0, similarities.size)
    }
}
