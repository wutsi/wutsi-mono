package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.sql.DataSource

@Service
class KpiPersister(
    private val ds: DataSource,
) {
    @Transactional
    fun persistStory(
        date: LocalDate,
        type: KpiType,
        storyId: Long,
        value: Long,
        source: TrafficSource = TrafficSource.ALL,
    ): Int {
        val sql = """
            INSERT INTO T_STORY_KPI(story_id, type, source, year, month, value)
                VALUES(
                    $storyId,
                    ${type.ordinal},
                    ${source.ordinal},
                    ${date.year},
                    ${date.month.value},
                    $value
                )
                ON DUPLICATE KEY UPDATE
                    value=$value
        """.trimIndent()

        val cnn = ds.connection
        return cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }
    }

    @Transactional
    fun persistProduct(
        date: LocalDate,
        type: KpiType,
        productId: Long,
        value: Long,
        source: TrafficSource = TrafficSource.ALL,
    ): Int {
        val sql = """
            INSERT INTO T_PRODUCT_KPI(product_id, type, source, year, month, value)
                VALUES(
                    $productId,
                    ${type.ordinal},
                    ${source.ordinal},
                    ${date.year},
                    ${date.month.value},
                    $value
                )
                ON DUPLICATE KEY UPDATE
                    value=$value
        """.trimIndent()

        val cnn = ds.connection
        return cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }
    }

    @Transactional
    fun persistUser(
        date: LocalDate,
        type: KpiType,
        userId: Long,
        source: TrafficSource,
    ): Int {
        val sql = """
            INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
                SELECT S.user_fk, K.type, K.source, K.year, K.month, SUM(K.value)
                    FROM T_STORY_KPI K JOIN T_STORY S ON K.story_id=S.id
                    WHERE S.user_fk=$userId
                        AND type=${type.ordinal}
                        AND source=${source.ordinal}
                        AND year=${date.year}
                        AND month=${date.month.value}
                    GROUP BY S.user_fk, K.type, K.source, K.year, K.month
            ON DUPLICATE KEY UPDATE
                    value=(
                        SELECT SUM(K.value)
                        FROM T_STORY_KPI K JOIN T_STORY S ON K.story_id=S.id
                        WHERE S.user_fk=$userId
                            AND type=${type.ordinal}
                            AND source=${source.ordinal}
                            AND year=${date.year}
                            AND month=${date.month.value}
                    )
        """.trimIndent()

        val cnn = ds.connection
        return cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }
    }

    @Transactional
    fun persistStorySubscriptions(date: LocalDate): Int {
        val sql = """
            INSERT INTO T_STORY_KPI(story_id, type, year, month, value, source)
            SELECT story_fk, ${KpiType.SUBSCRIPTION.ordinal}, ${date.year}, ${date.monthValue}, COUNT(*), 0
                FROM T_SUBSCRIPTION S
                WHERE YEAR(S.timestamp)=${date.year}
                    AND MONTH(S.timestamp)=${date.monthValue}
                    AND story_fk IS NOT NULL
                GROUP BY story_fk, YEAR(timestamp), MONTH(timestamp)
            ON DUPLICATE KEY UPDATE
                value=VALUES(value)
        """.trimIndent()

        val cnn = ds.connection
        return cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }
    }

    @Transactional
    fun persistUserSubscriptions(date: LocalDate): Int {
        val sql = """
            INSERT INTO T_USER_KPI(user_id, type, year, month, value, source)
            SELECT user_fk, ${KpiType.SUBSCRIPTION.ordinal}, ${date.year}, ${date.monthValue}, COUNT(*), 0
                FROM T_SUBSCRIPTION S
                WHERE YEAR(S.timestamp)=${date.year}
                    AND MONTH(S.timestamp)=${date.monthValue}
                GROUP BY user_fk, YEAR(timestamp), MONTH(timestamp)
            ON DUPLICATE KEY UPDATE
                value=VALUES(value)
        """.trimIndent()

        val cnn = ds.connection
        return cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.executeUpdate(sql)
            }
        }
    }
}
