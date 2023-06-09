package com.wutsi.blog.kpi.dao

import com.wutsi.blog.kpi.domain.MonthlyKpiEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MonthlyKpiRepository : CrudRepository<MonthlyKpiEntity, Long>
