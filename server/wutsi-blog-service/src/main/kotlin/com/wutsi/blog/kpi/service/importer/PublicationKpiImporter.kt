package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.story.dto.StoryStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.sql.DataSource

@Service
class PublicationKpiImporter(
    private val ds: DataSource,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long {
        val sql = """
            INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
                SELECT
                    0,
                    ${KpiType.PUBLICATION.ordinal},
                    ${TrafficSource.ALL.ordinal},
                    YEAR(published_date_time),
                    MONTH(published_date_time),
                    count(*)
                FROM T_STORY
                WHERE
                    deleted=false AND
                    status = ${StoryStatus.PUBLISHED.ordinal} AND
                    YEAR(published_date_time) = ${date.year} AND
                    MONTH(published_date_time) = ${date.year}
                GROUP BY
                    YEAR(published_date_time),
                    MONTH(published_date_time)
                ON DUPLICATE KEY UPDATE value=VALUES(value)
        """.trimIndent()

        val cnn = ds.connection
        return cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }.toLong()
    }
}
