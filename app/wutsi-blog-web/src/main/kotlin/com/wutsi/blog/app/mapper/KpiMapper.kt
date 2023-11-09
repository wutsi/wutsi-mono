package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.kpi.dto.StoryKpi
import com.wutsi.blog.kpi.dto.UserKpi
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KpiMapper {
    fun toKpiModel(kpi: StoryKpi) = KpiModel(
        id = kpi.id,
        targetId = kpi.storyId,
        type = kpi.type,
        value = kpi.value.toDouble(),
        date = LocalDate.of(kpi.year, kpi.month, 1),
        source = kpi.source,
    )

    fun toKpiModel(kpi: UserKpi) = KpiModel(
        id = kpi.id,
        targetId = kpi.userId,
        type = kpi.type,
        value = kpi.value.toDouble(),
        date = LocalDate.of(kpi.year, kpi.month, 1),
        source = kpi.source,
    )
}
