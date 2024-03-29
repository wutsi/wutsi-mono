package com.wutsi.blog.story.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.service.similarity.StorySimilarityFallbackStrategy
import com.wutsi.blog.story.service.similarity.StorySimilarityMLStrategy
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StorySimilarityServiceTest {
    private lateinit var algorithm: StorySimilarityMLStrategy

    private lateinit var fallback: StorySimilarityFallbackStrategy

    private lateinit var service: StorySimilarityService

    @BeforeEach
    fun setUp() {
        algorithm = mock()
        fallback = mock()
        service = StorySimilarityService(algorithm, fallback, DefaultKVLogger())
    }

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
