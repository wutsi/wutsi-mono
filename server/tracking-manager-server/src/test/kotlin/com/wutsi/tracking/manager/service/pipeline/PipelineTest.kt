package com.wutsi.tracking.manager.service.pipeline

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Test

internal class PipelineTest {
    @Test
    fun filter() {
        val track = TrackEntity()

        val step1 = mock<Filter>()
        doReturn(track).whenever(step1).filter(track)

        val step2 = mock<Filter>()
        doReturn(track).whenever(step2).filter(track)

        val pipeline = Pipeline(listOf(step1, step2))

        pipeline.filter(track)

        verify(step1).filter(track)
        verify(step2).filter(track)
    }
}
