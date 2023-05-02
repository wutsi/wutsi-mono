package com.wutsi.blog.story.service.sort.algo

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.story.service.ViewService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class MostViewedSortAlgorithmTest {
    @Mock
    private lateinit var service: ViewService

    @InjectMocks
    private lateinit var algo: MostViewedSortAlgorithm

    @Test
    fun sort() {
        val storyIds = arrayListOf(4L, 3L, 2L, 1L)
        doReturn(deviceIds(10)).whenever(service).get(1L)
        doReturn(deviceIds(9)).whenever(service).get(2L)
        doReturn(deviceIds(8)).whenever(service).get(3L)
        doReturn(deviceIds(7)).whenever(service).get(4L)

        val request = SortStoryRequest(storyIds = storyIds, deviceId = "1111")
        val response = algo.sort(request)

        assertEquals(4, response.size)
        assertEquals(1L, response[0])
        assertEquals(2L, response[1])
        assertEquals(3L, response[2])
        assertEquals(4L, response[3])
    }

    @Test
    fun sortWithStoriesWithNoStats() {
        val storyIds = arrayListOf(4L, 3L, 2L, 1L, 50L, 60L)
        doReturn(deviceIds(10)).whenever(service).get(1L)
        doReturn(deviceIds(9)).whenever(service).get(2L)
        doReturn(deviceIds(8)).whenever(service).get(3L)
        doReturn(deviceIds(7)).whenever(service).get(4L)

        val request = SortStoryRequest(storyIds = storyIds)
        val response = algo.sort(request)

        assertEquals(6, response.size)
        assertEquals(1L, response[0])
        assertEquals(2L, response[1])
        assertEquals(3L, response[2])
        assertEquals(4L, response[3])
        assertEquals(50L, response[4])
        assertEquals(60L, response[5])
    }

    private fun deviceIds(count: Int): Set<String> {
        val result = mutableSetOf<String>()
        for (i in 1..count) {
            result.add(UUID.randomUUID().toString())
        }
        return result
    }
}
