package com.wutsi.blog.story.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.service.recommendation.StoryRecommenderFallbackStrategy
import com.wutsi.blog.story.service.recommendation.StoryRecommenderMLStrategy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StoryRecommendationServiceTest {
    @MockBean
    private lateinit var algorithm: StoryRecommenderMLStrategy

    @MockBean
    private lateinit var fallback: StoryRecommenderFallbackStrategy

    @Autowired
    private lateinit var service: StoryRecommendationService

    @Test
    fun recommend() {
        // GIVEN
        doReturn(listOf(11L, 22L)).whenever(algorithm).recommend(any())

        // WHEN
        val request = RecommendStoryRequest(readerId = 1L, deviceId = "1111", limit = 100)
        val result = service.recommend(request)

        // THEN
        verify(algorithm).recommend(request)
        verify(fallback, never()).recommend(request)
        assertEquals(listOf(11L, 22L), result)
    }

    @Test
    fun fallback() {
        // GIVEN
        doReturn(listOf<Long>()).whenever(fallback).recommend(any())
        doReturn(listOf(11L, 22L)).whenever(fallback).recommend(any())

        // WHEN
        val request = RecommendStoryRequest(readerId = 1L, deviceId = "1111", limit = 100)
        val result = service.recommend(request)

        // THEN
        verify(algorithm).recommend(request)
        verify(fallback).recommend(request)
        assertEquals(listOf(11L, 22L), result)
    }
}
