package com.wutsi.marketplace.access.job

import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.endpoint.AbstractLanguageAwareControllerTest
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ImportTodaySalesKpiJob.sql"])
internal class ImportYesterdaySalesKpiJobTest : AbstractLanguageAwareControllerTest() {
    @Autowired
    private lateinit var job: ImportYesterdaySalesKpiJob

    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var dao: ProductRepository

    @Test
    fun run() {
        // GIVEN
        val date = LocalDate.now()
        setupCsv(
            csv = """
                business_id,product_id,total_orders,total_units,total_value,total_views
                1,100,3,6,9000,10
                1,101,1,1,500,11
                2,200,1,1,1500,20
                9,9999,1,1,1500,999
            """.trimIndent(),
            date = date.minusDays(1),
        )
        setupCsv(
            csv = """
                business_id,product_id,total_orders,total_units,total_value,total_views
                1,100,5,10,20000,15
            """.trimIndent(),
            date = date,
        )

        // WHEN
        job.run()

        // THEN
        assertKpi(100, 5, 10, 20000, 15)
        assertKpi(101, 1, 1, 500, 11)
        assertKpi(102, 0, 0, 0, 0)
        assertKpi(103, 0, 0, 0, 0)
        assertKpi(199, 0, 0, 0, 0)
        assertKpi(200, 1, 1, 1500, 20)
    }

    private fun setupCsv(csv: String, date: LocalDate) {
        val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/sales.csv"
        storage.store(path, ByteArrayInputStream(csv.toByteArray()))
    }

    private fun assertKpi(id: Long, totalOrders: Long, totalUnits: Long, totalSales: Long, totalViews: Long) {
        val product = dao.findById(id).get()
        assertEquals(totalOrders, product.totalOrders)
        assertEquals(totalUnits, product.totalUnits)
        assertEquals(totalSales, product.totalSales)
        assertEquals(totalViews, product.totalViews)
    }
}
