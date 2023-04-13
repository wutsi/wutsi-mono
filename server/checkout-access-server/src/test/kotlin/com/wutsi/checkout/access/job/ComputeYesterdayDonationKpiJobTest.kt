package com.wutsi.checkout.access.job

import com.amazonaws.util.IOUtils
import com.wutsi.checkout.access.dao.BusinessRepository
import com.wutsi.checkout.access.dao.DonationKpiRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ComputeYesterdayDonationKpiJob.sql"])
internal class ComputeYesterdayDonationKpiJobTest {
    @Autowired
    private lateinit var job: ComputeYesterdayDonationKpiJob

    @Autowired
    private lateinit var dao: DonationKpiRepository

    @Autowired
    private lateinit var businessDao: BusinessRepository

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDirectory: String

    @BeforeEach
    fun setUp() {
        File(storageDirectory).deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        val yesterday = LocalDate.now().minusDays(1)

        // WEN
        job.run()

        // THEN
        assertKpi(3, 8500, 1, yesterday)
        assertKpi(1, 1500, 2, yesterday)

        val business1 = businessDao.findById(1).get()
        assertEquals(3, business1.totalDonations)
        assertEquals(8500, business1.totalDonationValue)

        val business2 = businessDao.findById(2).get()
        assertEquals(1, business2.totalDonations)
        assertEquals(1500, business2.totalDonationValue)

        val input =
            FileInputStream(File("$storageDirectory/kpi/" + yesterday.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/donations.csv"))
        input.use {
            assertEquals(
                """
                    business_id,total_donations,total_value
                    1,3,8500
                    2,1,1500
                """.trimIndent(),
                IOUtils.toString(input).trimIndent(),
            )
        }
    }

    private fun assertKpi(
        totalOrders: Long,
        totalValue: Long,
        businessId: Long,
        date: LocalDate,
    ) {
        val kpi = dao.findByBusinessAndDate(
            business = businessDao.findById(businessId).get(),
            date = Date.from(date.atStartOfDay(ZoneId.of("UTC")).toInstant()),
        )
        assertEquals(totalOrders, kpi.get().totalDonations)
        assertEquals(totalValue, kpi.get().totalValue)
    }
}
