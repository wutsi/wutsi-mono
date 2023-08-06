package com.wutsi.ml.embedding.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.similarity.dto.SearchSimilarityRequest
import com.wutsi.blog.similarity.dto.SearchSimilarityResponse
import com.wutsi.blog.similarity.dto.Similarity
import com.wutsi.ml.embedding.service.TfIdfConfig
import com.wutsi.ml.embedding.service.TfIdfSimilarityService
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cache.Cache
import org.springframework.http.HttpStatus
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SearchSimilarStoriesQueryTest {
    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var service: TfIdfSimilarityService

    @MockBean
    private lateinit var cache: Cache

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
        verify(cache).invalidate()

        doReturn(null).whenever(cache).get(any(), any<Class<SearchSimilarityRequest>>())
    }

    @Test
    fun search() {
        // WHEN
        val request = SearchSimilarityRequest(
            ids = listOf(300L),
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
    fun searchFromCache() {
        val cached = SearchSimilarityResponse(
            similarities = listOf(
                Similarity(1L, 0.9)
            )
        )
        doReturn(cached).whenever(cache).get(any(), any<Class<SearchSimilarityRequest>>())

        // WHEN
        val request = SearchSimilarityRequest(
            ids = listOf(300L),
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val similarities = response.body!!
        assertEquals(cached, similarities)
    }

    @Test
    fun searchMultipleStories() {
        // WHEN
        val request = SearchSimilarityRequest(
            ids = listOf(100L, 200L),
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val similarities = response.body!!.similarities
        assertEquals(2, similarities.size)

        assertEquals(300L, similarities[0].id)
        assertEquals(0.9, similarities[0].score)

        assertEquals(400L, similarities[1].id)
        assertEquals(0.1, similarities[1].score)
    }

    @Test
    fun searchWithSimilarIds() {
        // WHEN
        val request = SearchSimilarityRequest(
            ids = listOf(300L),
            similarIds = listOf(400L, 9999L),
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val similarities = response.body!!.similarities
        assertEquals(1, similarities.size)

        assertEquals(400L, similarities[0].id)
        assertEquals(0.3, similarities[0].score)
    }

    @Test
    fun badId() {
        // WHEN
        val request = SearchSimilarityRequest(
            ids = listOf(99999L),
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
