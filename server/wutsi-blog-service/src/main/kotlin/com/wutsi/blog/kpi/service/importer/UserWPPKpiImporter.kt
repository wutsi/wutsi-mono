package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.sql.DataSource

@Service
class UserWPPKpiImporter(
    private val ds: DataSource,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long {
        val sql = """
            INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
                SELECT
                    0,
                    ${KpiType.USER_WPP.ordinal},
                    ${TrafficSource.ALL.ordinal},
                    YEAR(creation_date_time),
                    MONTH(creation_date_time),
                    count(*)
                FROM T_USER
                WHERE
                    suspended=false AND
                    blog=true AND
                    YEAR(creation_date_time) = ${date.year} AND
                    MONTH(creation_date_time) = ${date.year}
                GROUP BY
                    YEAR(creation_date_time),
                    MONTH(creation_date_time)
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
