package com.wutsi.blog.story.service.sort.algo

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.story.service.ViewService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class RecommendedSortAlgorithmTest {
    @Mock
    private lateinit var service: ViewService

    @InjectMocks
    private lateinit var algo: RecommendedSortAlgorithm

    @Test
    fun sort() {
        val storyIds = arrayListOf(1L, 2L, 3L, 4L)
        doReturn(true).whenever(service).contains(any(), eq(1L))
        doReturn(true).whenever(service).contains(any(), eq(2L))

        val request = SortStoryRequest(storyIds = storyIds, deviceId = "11111")
        val response = algo.sort(request)

        assertEquals(4, response.size)
        assertEquals(3L, response[0])
        assertEquals(4L, response[1])
        assertEquals(1L, response[2])
        assertEquals(2L, response[3])
    }
}
