package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.sql.DataSource

@Service
class SalesValueKpiImporter(
    private val ds: DataSource,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long {
        val sql = """
            INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
                SELECT
                    S.user_fk,
                    ${KpiType.SALES.ordinal},
                    ${TrafficSource.ALL.ordinal},
                    YEAR(T.creation_date_time),
                    MONTH(T.creation_date_time),
                    sum(T.amount)
                FROM T_TRANSACTION T JOIN T_STORE S ON T.store_fk=S.id
                WHERE
                    type=${TransactionType.CHARGE.ordinal} AND
                    status=${Status.SUCCESSFUL.ordinal}  AND
                    YEAR(T.creation_date_time) = ${date.year} AND
                    MONTH(T.creation_date_time) = ${date.year}
                GROUP BY
                    S.user_fk,
                    YEAR(T.creation_date_time),
                    MONTH(T.creation_date_time)
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
