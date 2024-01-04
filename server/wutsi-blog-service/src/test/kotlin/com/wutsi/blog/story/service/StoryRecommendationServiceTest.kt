package com.wutsi.blog.story.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.service.recommendation.StoryRecommenderFallbackStrategy
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StoryRecommendationServiceTest {
    private lateinit var fallback: StoryRecommenderFallbackStrategy

    private lateinit var service: StoryRecommendationService

    @BeforeEach
    fun setUp() {
        fallback = mock()
        service = StoryRecommendationService(fallback, DefaultKVLogger())
    }

    @Test
    fun recommend() {
        // GIVEN
        doReturn(listOf(11L, 22L)).whenever(fallback).recommend(any())

        // WHEN
        val request = RecommendStoryRequest(readerId = 1L, deviceId = "1111", limit = 100)
        val result = service.recommend(request)

        // THEN
        verify(fallback).recommend(request)
        assertEquals(listOf(11L, 22L), result)
    }
}
