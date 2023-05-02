package com.wutsi.blog.story.service.sort

import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.platform.core.logging.KVLogger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class SortServiceTest {
    @Mock
    private lateinit var algo: SortAlgorithm

    @Mock
    private lateinit var algos: SortAlgorithmFactory

    @Mock
    private lateinit var logger: KVLogger

    @InjectMocks
    private lateinit var service: SortService

    @Test
    fun sort() {
        val request = SortStoryRequest(
            storyIds = arrayListOf(4L, 3L, 2L, 1L),
            deviceId = "device-1",
            algorithm = SortAlgorithmType.most_recent,
            userId = 1L,
        )

        val sortedIds = arrayListOf(1L, 2L, 3L, 4L)
        `when`(algos.get(request.algorithm)).thenReturn(algo)
        `when`(algo.sort(request)).thenReturn(sortedIds)

        val response = service.sort(request)

        assertEquals(sortedIds, response.storyIds)
    }
}
