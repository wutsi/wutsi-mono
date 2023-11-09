package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StoryKpiRepository : CrudRepository<StoryKpiEntity, Long> {
    fun findByStoryIdAndTypeAndYearAndMonthAndSource(
        storyId: Long,
        type: KpiType,
        year: Int,
        month: Int,
        source: TrafficSource,
    ): Optional<StoryKpiEntity>

    @Query("SELECT SUM(K.value) FROM StoryKpiEntity K WHERE K.storyId=?1 AND K.type=?2 AND source=?3")
    fun sumValueByStoryIdAndTypeAndSource(storyId: Long, type: KpiType, source: TrafficSource): Long?
}
