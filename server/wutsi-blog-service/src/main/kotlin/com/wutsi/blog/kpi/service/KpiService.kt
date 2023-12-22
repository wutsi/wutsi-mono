package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dao.SearchStoryKpiMonthlyQueryBuilder
import com.wutsi.blog.kpi.dao.SearchUserKpiMonthlyQueryBuilder
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.domain.UserKpiEntity
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.service.importer.KpiImporterSet
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.logging.KVLogger
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class KpiService(
    private val logger: KVLogger,
    private val em: EntityManager,
    private val kpiImporterSet: KpiImporterSet,
) {
    fun replay(year: Int, month: Int? = null) {
        val now = LocalDate.now(ZoneId.of("UTC"))
        logger.add("command", "ReplayKpiCommand")

        var date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            import(date)

            date = date.plusMonths(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }
    }

    fun import(date: LocalDate): Long =
        kpiImporterSet.import(date)

    fun search(request: SearchStoryKpiRequest): List<StoryKpiEntity> {
        val builder = SearchStoryKpiMonthlyQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, StoryKpiEntity::class.java)
        Predicates.setParameters(query, params)

        return query.resultList as List<StoryKpiEntity>
    }

    fun search(request: SearchUserKpiRequest): List<UserKpiEntity> {
        val builder = SearchUserKpiMonthlyQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, UserKpiEntity::class.java)
        Predicates.setParameters(query, params)

        return query.resultList as List<UserKpiEntity>
    }
}
