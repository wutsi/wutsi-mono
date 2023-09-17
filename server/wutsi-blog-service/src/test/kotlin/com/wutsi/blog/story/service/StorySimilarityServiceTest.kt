package com.wutsi.blog.story.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.service.similarity.StorySimilarityFallbackStrategy
import com.wutsi.blog.story.service.similarity.StorySimilarityMLStrategy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StorySimilarityServiceTest {
    @MockBean
    private lateinit var algorithm: StorySimilarityMLStrategy

    @MockBean
    private lateinit var fallback: StorySimilarityFallbackStrategy

    @Autowired
    private lateinit var service: StorySimilarityService

    @Test
    fun recommend() {
        // GIVEN
        doReturn(listOf(11L, 22L)).whenever(algorithm).search(any())

        // WHEN
        val request = SearchSimilarStoryRequest(storyIds = listOf(10L, 11L), limit = 100)
        val result = service.search(request)

        // THEN
        verify(algorithm).search(request)
        verify(fallback, never()).search(request)
        assertEquals(listOf(11L, 22L), result)
    }

    @Test
    fun fallback() {
        // GIVEN
        doReturn(listOf<Long>()).whenever(fallback).search(any())
        doReturn(listOf(11L, 22L)).whenever(fallback).search(any())

        // WHEN
        val request = SearchSimilarStoryRequest(storyIds = listOf(10L, 11L), limit = 100)
        val result = service.search(request)

        // THEN
        verify(algorithm).search(request)
        verify(fallback).search(request)
        assertEquals(listOf(11L, 22L), result)
    }
}
