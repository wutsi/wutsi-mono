package com.wutsi.ml.similarity.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ml.similarity.dto.Item
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.ml.similarity.dto.SimilarityModelType
import com.wutsi.ml.similarity.model.SimilarityModel
import com.wutsi.ml.similarity.model.SimilarityModelFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cache.Cache
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchSimilarityQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var factory: SimilarityModelFactory

    @MockBean
    private lateinit var cache: Cache

    @Test
    fun search() {
        // GIVEN
        val model = mock<SimilarityModel>()
        doReturn(model).whenever(factory).get(any())

        doReturn(null).whenever(cache).get(any(), any<Class<*>>())

        val result = SearchSimilarityResponse(
            listOf(
                Item(1, .9),
                Item(2, .4),
                Item(3, .5),
            ),
        )
        doReturn(result).whenever(model).search(any())

        // WHEN
        val request = SearchSimilarityRequest(
            itemIds = listOf(300L),
            model = SimilarityModelType.AUTHOR_TIFDF,
            limit = 100,
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(result.items, response.body!!.items)

        verify(cache).put(any<String>(), eq(result))
    }

    @Test
    fun fromCache() {
        // GIVEN
        val model = mock<SimilarityModel>()
        doReturn(model).whenever(factory).get(any())

        val result = SearchSimilarityResponse(
            listOf(
                Item(1, .9),
                Item(2, .4),
                Item(3, .5),
            ),
        )
        doReturn(result).whenever(cache).get(any(), any<Class<*>>())

        // WHEN
        val request = SearchSimilarityRequest(
            itemIds = listOf(300L),
            model = SimilarityModelType.AUTHOR_TIFDF,
            limit = 100,
        )
        val response = rest.postForEntity(
            "/v1/similarities/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(result.items, response.body!!.items)

        verify(factory, never()).get(any())
        verify(cache, never()).put(any(), any())
    }
}
