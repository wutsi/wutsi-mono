package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dao.SearchStoryKpiMonthlyQueryBuilder
import com.wutsi.blog.kpi.dao.SearchUserKpiMonthlyQueryBuilder
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.domain.UserKpiEntity
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.service.importer.ClickCountKpiImporter
import com.wutsi.blog.kpi.service.importer.ClickKpiImporter
import com.wutsi.blog.kpi.service.importer.ClickRateKpiImporter
import com.wutsi.blog.kpi.service.importer.CommentKpiImporter
import com.wutsi.blog.kpi.service.importer.CouterKpiUpdater
import com.wutsi.blog.kpi.service.importer.DurationKpiImporter
import com.wutsi.blog.kpi.service.importer.EmailKpiImporter
import com.wutsi.blog.kpi.service.importer.LikeKpiImporter
import com.wutsi.blog.kpi.service.importer.ReadKpiImporter
import com.wutsi.blog.kpi.service.importer.ReaderCountKpiImporter
import com.wutsi.blog.kpi.service.importer.ReaderKpiImporter
import com.wutsi.blog.kpi.service.importer.SourceImporter
import com.wutsi.blog.kpi.service.importer.SubscriptionKpiImporter
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

    private val clickImporter: ClickKpiImporter,
    private val clickCountImport: ClickCountKpiImporter,
    private val readerImporter: ReaderKpiImporter,
    private val readerCouterKpiImporter: ReaderCountKpiImporter,
    private val durationImporter: DurationKpiImporter,
    private val sourceImporter: SourceImporter,
    private val subscriptionImporter: SubscriptionKpiImporter,
    private val readImporter: ReadKpiImporter,
    private val emailInporter: EmailKpiImporter,
    private val counterUpdater: CouterKpiUpdater,
    private val clickRateKpiImporter: ClickRateKpiImporter,
    private val likeImporter: LikeKpiImporter,
    private val commentImporter: CommentKpiImporter
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
        sourceImporter.import(date) +
            durationImporter.import(date) +
            clickImporter.import(date) +
            clickCountImport.import(date) +
            readerImporter.import(date) +
            readerCouterKpiImporter.import(date) +
            clickRateKpiImporter.import(date) + // IMPORT: MUST be after click and reader importers
            readImporter.import(date) +
            emailInporter.import(date) +
            likeImporter.import(date) +
            commentImporter.import(date) +

            subscriptionImporter.import(date) +
            counterUpdater.import(date) // IMPORTANT: MUST be last

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
