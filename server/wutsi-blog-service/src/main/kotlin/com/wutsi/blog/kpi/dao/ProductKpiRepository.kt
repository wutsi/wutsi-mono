package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.domain.ProductKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductKpiRepository : CrudRepository<ProductKpiEntity, Long> {
    fun findByProductIdAndTypeAndYearAndMonthAndSource(
        storyId: Long,
        type: KpiType,
        year: Int,
        month: Int,
        source: TrafficSource,
    ): Optional<ProductKpiEntity>

    @Query("SELECT SUM(K.value) FROM ProductKpiEntity K WHERE K.productId=?1 AND K.type=?2 AND K.source=?3")
    fun sumValueByProductIdAndTypeAndSource(productId: Long, type: KpiType, source: TrafficSource): Long?
}
