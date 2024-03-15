package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.domain.AdsKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AdsKpiRepository : CrudRepository<AdsKpiEntity, String> {
    @Query("SELECT SUM(K.value) FROM AdsKpiEntity K WHERE K.adsId=?1 AND K.type=?2 AND K.source=?3")
    fun sumValueByAdsIdAndTypeAndSource(adsId: String, type: KpiType, source: TrafficSource): Long?
}
