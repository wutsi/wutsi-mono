package com.wutsi.checkout.access.service

import com.wutsi.checkout.access.dto.SearchDonationKpiRequest
import com.wutsi.checkout.access.entity.DonationKpiEntity
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.OrderType
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.sql.ResultSet
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import javax.persistence.EntityManager
import javax.persistence.Query
import javax.sql.DataSource
import javax.transaction.Transactional

@Service
class DonationKpiService(
    private val em: EntityManager,
    private val ds: DataSource,
    private val storage: StorageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DonationKpiService::class.java)
    }

    @Transactional
    fun computeFromOrders(date: LocalDate): Long {
        val sql =
            """
                INSERT INTO T_KPI_DONATION(date, business_fk, total_donations, total_value)
                    SELECT date, business_fk, total_donations, total_value FROM
                    (
                        SELECT
                                DATE(O.created) AS date,
                                O.business_fk,
                                COUNT(O.id) AS total_donations,
                                SUM(O.total_price) AS  total_value
                            FROM T_ORDER O
                            WHERE
                                DATE(O.created) = '$date'
                                AND O.type=${OrderType.DONATION.ordinal}
                                AND O.status NOT IN (
                                    ${OrderStatus.UNKNOWN.ordinal},
                                    ${OrderStatus.PENDING.ordinal},
                                    ${OrderStatus.EXPIRED.ordinal}
                                )
                            GROUP BY DATE(O.created), O.business_fk
                    ) TMP
                ON DUPLICATE KEY UPDATE total_donations=TMP.total_donations, total_value=TMP.total_value
            """.trimIndent()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.prepareStatement(sql)
            stmt.use {
                val result = stmt.executeUpdate().toLong()

                LOGGER.info("$result KPIs computed from Orders")
                return result
            }
        }
    }

    /**
     * Export donations to CSV.
     * The CSV file has the following columns:
     *   - business_id
     *   - total_donations
     *   - total_value
     */
    fun export(date: LocalDate): Long {
        val sql = """
            SELECT K.business_fk, SUM(K.total_donations) AS total_donations, SUM(K.total_value) as total_value
            FROM T_KPI_DONATION K
            GROUP BY K.business_fk
        """.trimIndent()

        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.prepareStatement(sql)
            stmt.use {
                val rs = stmt.executeQuery()
                rs.use {
                    return export(date, rs)
                }
            }
        }
    }

    private fun export(date: LocalDate, rs: ResultSet): Long {
        // Store to file
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        var result: Long = 0
        try {
            val writer: BufferedWriter = Files.newBufferedWriter(file.toPath())
            writer.use {
                val printer = CSVPrinter(
                    writer,
                    CSVFormat.DEFAULT
                        .builder()
                        .setHeader(
                            "business_id",
                            "total_donations",
                            "total_value",
                        )
                        .build(),
                )
                printer.use {
                    while (rs.next()) {
                        printer.printRecord(
                            rs.getLong("business_fk"),
                            rs.getLong("total_donations"),
                            rs.getLong("total_value"),
                        )
                        result++
                    }
                    printer.flush()
                }
            }

            // Store file to S3
            val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/donations.csv"
            val input = FileInputStream(file)
            input.use {
                val url = storage.store(path, input, "text/csv", null, "utf-8")
                LOGGER.info("Donations KPIs stored to $url")
            }
            return result
        } finally {
            file.delete() // Delete  the file
        }
    }

    fun search(request: SearchDonationKpiRequest): List<DonationKpiEntity> {
        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        val kpis = query.resultList as List<DonationKpiEntity>
        if (request.aggregate && kpis.isNotEmpty()) {
            return listOf(
                DonationKpiEntity(
                    date = kpis[0].date,
                    totalValue = kpis.sumOf { it.totalValue },
                    totalDonations = kpis.sumOf { it.totalDonations },
                ),
            )
        }

        return kpis
    }

    private fun sql(request: SearchDonationKpiRequest): String {
        val select = select(request)
        val where = where(request)
        val orderBy = orderBy(request)
        return "$select WHERE $where $orderBy"
    }

    private fun select(request: SearchDonationKpiRequest): String =
        "SELECT a FROM DonationKpiEntity a"

    private fun where(request: SearchDonationKpiRequest): String {
        val criteria = mutableListOf<String>()

        if (request.businessId != null) {
            criteria.add("a.business.id=:business_id")
        }
        if (request.fromDate != null) {
            criteria.add("a.date >= :from_date")
        }
        if (request.toDate != null) {
            criteria.add("a.date <= :to_date")
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchDonationKpiRequest, query: Query) {
        if (request.businessId != null) {
            query.setParameter("business_id", request.businessId)
        }
        if (request.fromDate != null) {
            query.setParameter("from_date", Date.from(request.fromDate.atStartOfDay().toInstant(ZoneOffset.UTC)))
        }
        if (request.toDate != null) {
            query.setParameter("to_date", Date.from(request.toDate.atStartOfDay().toInstant(ZoneOffset.UTC)))
        }
    }

    private fun orderBy(request: SearchDonationKpiRequest): String =
        if (request.aggregate) {
            ""
        } else {
            "ORDER BY a.date"
        }
}
