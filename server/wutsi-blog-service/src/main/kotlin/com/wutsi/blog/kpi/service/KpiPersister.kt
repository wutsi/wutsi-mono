package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dto.KpiType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.sql.DataSource

@Service
class KpiPersister(
    private val ds: DataSource,
) {
    @Transactional
    fun persist(date: LocalDate, type: KpiType, storyId: Long, value: Long) {
        val sql = """
            INSERT INTO T_KPI_MONTHLY(story_id, type, year, month, value)
                VALUES(
                    $storyId,
                    ${type.ordinal},
                    ${date.year},
                    ${date.month.value},
                    $value
                )
                ON DUPLICATE KEY UPDATE
                    value=$value
        """.trimIndent()

        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }
    }
}
