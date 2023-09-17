package com.wutsi.blog.user.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.service.recommendation.UserRecommenderFallbackStrategy
import com.wutsi.blog.user.service.recommendation.UserRecommenderMLStrategy
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRecommendationServiceTest {
    private lateinit var algorithm: UserRecommenderMLStrategy
    private lateinit var fallback: UserRecommenderFallbackStrategy
    private lateinit var service: UserRecommendationService

    @BeforeEach
    fun setUp() {
        algorithm = mock()
        fallback = mock()
        service = UserRecommendationService(algorithm, fallback, DefaultKVLogger())
    }

    @Test
    fun recommend() {
        // GIVEN
        doReturn(listOf(11L, 22L)).whenever(algorithm).recommend(any())

        // WHEN
        val request = RecommendUserRequest(readerId = 1L, deviceId = "1111", limit = 100)
        val result = service.recommend(request)

        // THEN
        verify(algorithm).recommend(request)
        verify(fallback, never()).recommend(request)
        kotlin.test.assertEquals(listOf(11L, 22L), result)
    }

    @Test
    fun fallback() {
        // GIVEN
        doReturn(listOf<Long>()).whenever(fallback).recommend(any())
        doReturn(listOf(11L, 22L)).whenever(fallback).recommend(any())

        // WHEN
        val request = RecommendUserRequest(readerId = 1L, deviceId = "1111", limit = 100)
        val result = service.recommend(request)

        // THEN
        verify(algorithm).recommend(request)
        verify(fallback).recommend(request)
        kotlin.test.assertEquals(listOf(11L, 22L), result)
    }
}
