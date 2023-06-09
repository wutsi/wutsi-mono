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

    @Transactional
    fun updateStory(storyId: Long, type: KpiType) {
        val column = when (type) {
            KpiType.READ -> "read_count"
            else -> return
        }

        val sql = """
            UPDATE T_STORY S
                SET $column = (SELECT sum(K.value) FROM T_KPI_MONTHLY K WHERE type=${type.ordinal} AND K.story_id=S.id)
                WHERE S.id=$storyId
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
