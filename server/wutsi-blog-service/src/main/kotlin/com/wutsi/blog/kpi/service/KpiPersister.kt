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
    fun persistStory(date: LocalDate, type: KpiType, storyId: Long, value: Long) {
        val sql = """
            INSERT INTO T_STORY_KPI(story_id, type, year, month, value)
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
    fun persistUser(date: LocalDate, type: KpiType, userId: Long) {
        val sql = """
            INSERT INTO T_USER_KPI(user_id, type, year, month, value)
                SELECT S.user_fk, K.type, K.year, K.month, SUM(K.value)
                    FROM T_STORY_KPI K JOIN T_STORY S ON K.story_id=S.id
                    WHERE S.user_fk=$userId
                        AND type=${type.ordinal}
                        AND year=${date.year}
                        AND month=${date.month.value}
                    GROUP BY S.user_fk, K.type, K.year, K.month
            ON DUPLICATE KEY UPDATE
                    value=(
                        SELECT SUM(K.value)
                        FROM T_STORY_KPI K JOIN T_STORY S ON K.story_id=S.id
                        WHERE S.user_fk=$userId
                            AND type=${type.ordinal}
                            AND year=${date.year}
                            AND month=${date.month.value}
                    )
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
