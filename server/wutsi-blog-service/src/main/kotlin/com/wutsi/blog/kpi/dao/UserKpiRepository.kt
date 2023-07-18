package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.domain.UserKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserKpiRepository : CrudRepository<UserKpiEntity, Long> {
    fun findByUserIdAndTypeAndYearAndMonth(
        userId: Long,
        type: KpiType,
        year: Int,
        month: Int,
    ): Optional<UserKpiEntity>

    @Query("SELECT SUM(K.value) FROM UserKpiEntity K WHERE K.userId=?1 AND K.type=?2")
    fun sumValueByUserIdAndType(userId: Long, type: KpiType): Long?
}
