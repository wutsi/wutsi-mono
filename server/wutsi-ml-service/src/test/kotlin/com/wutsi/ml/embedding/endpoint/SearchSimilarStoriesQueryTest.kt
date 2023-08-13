package com.wutsi.ml.embedding.endpoint

import com.wutsi.ml.embedding.dto.SearchSimilarStoryRequest
import com.wutsi.ml.embedding.dto.SearchSimilarStoryResponse
import com.wutsi.ml.embedding.service.TfIdfConfig
import com.wutsi.ml.embedding.service.TfIdfEmbeddingService
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

    @Autowired
    private lateinit var service: TfIdfEmbeddingService

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
        service.init()
    }

    @Test
    fun search() {
        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(300L),
        )
        val response = rest.postForEntity(
            "/v1/embeddings/queries/search-similarities",
            request,
            SearchSimilarStoryResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val stories = response.body!!.stories
        assertEquals(3, stories.size)

        assertEquals(200L, stories[0].id)
        assertEquals(0.9, stories[0].score)

        assertEquals(400L, stories[1].id)
        assertEquals(0.3, stories[1].score)

        assertEquals(100L, stories[2].id)
        assertEquals(0.2, stories[2].score)
    }

    @Test
    fun searchMultipleStories() {
        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(100L, 200L),
        )
        val response = rest.postForEntity(
            "/v1/embeddings/queries/search-similarities",
            request,
            SearchSimilarStoryResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val stories = response.body!!.stories
        assertEquals(2, stories.size)

        assertEquals(300L, stories[0].id)
        assertEquals(0.9, stories[0].score)

        assertEquals(400L, stories[1].id)
        assertEquals(0.1, stories[1].score)
    }

    @Test
    fun badId() {
        // WHEN
        val request = SearchSimilarStoryRequest(
            storyIds = listOf(99999L),
        )
        val response = rest.postForEntity(
            "/v1/embeddings/queries/search-similarities",
            request,
            SearchSimilarStoryResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val stories = response.body!!.stories
        assertEquals(0, stories.size)
    }
}
