package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.domain.KpiMonthlyEntity
import com.wutsi.blog.kpi.dto.KpiType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface KpiMonthlyRepository : CrudRepository<KpiMonthlyEntity, Long> {
    fun findByStoryIdAndTypeAndYearAndMonth(
        storyId: Long,
        type: KpiType,
        year: Int,
        month: Int,
    ): Optional<KpiMonthlyEntity>

    @Query("SELECT SUM(K.value) FROM KpiMonthlyEntity  K WHERE K.storyId=?1 AND K.type=?2")
    fun sumValueByStoryIdAndType(storyId: Long, type: KpiType): Long?
}
