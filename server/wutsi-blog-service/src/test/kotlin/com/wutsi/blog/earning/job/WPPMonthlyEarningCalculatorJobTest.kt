package com.wutsi.blog.earning.job

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.dao.UserKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.util.DateUtils
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.time.Clock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/earning/WPPEarningCalculatorJob.sql"])
class WPPMonthlyEarningCalculatorJobTest {
    @Autowired
    private lateinit var job: WPPMonthlyEarningCalculatorJob

    @MockBean
    private lateinit var clock: Clock

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storyKpiDao: StoryKpiRepository

    @Autowired
    private lateinit var userKpiDao: UserKpiRepository

    @Value("\${spring.mail.port}")
    private lateinit var port: String

    private lateinit var smtp: GreenMail

    private val date = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-20")

    @Value("\${wutsi.application.wpp.monhtly-budget}")
    private lateinit var monthlyBudget: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()

        doReturn(date.time).whenever(clock).millis()

        smtp = GreenMail(ServerSetup.SMTP.port(port.toInt()))
        smtp.setUser("wutsi", "secret")
        smtp.start()

        doReturn(date.time).whenever(clock).millis()
    }

    @AfterEach
    fun tearDown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
    }

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        assertFile(
            File("$storageDir/earnings/2020/01/wpp-story.csv"),
            """
                story_id,user_id,read_count,reader_count,like_count,comment_count,click_count,subscriber_count,read_time,earning_ratio,earning_adjustment,engagement_ratio,earnings,bonus,total
                100,111,100,100,15,5,10,0,40000,0.3719200371920037,1.0,0.22900763358778625,74380,6420,80800
                200,211,475,475,60,30,0,0,30000,0.2789400278940028,0.9,0.6870229007633588,50200,19280,69480
                201,211,50,40,5,1,1,0,10000,0.09298000929800093,0.4,0.05343511450381679,7430,1490,8920
                202,211,75,60,2,0,2,0,7500,0.0697350069735007,0.8,0.030534351145038167,11150,850,12000
                300,311,350,300,0,0,0,0,20000,0.18596001859600186,0.7714285714285714,0.0,28690,0,28690
                301,311,10,10,0,0,0,0,50,4.6490004649000463E-4,0.9,0.0,80,0,80
            """.trimIndent(),
        )
        assertStoryKpi(74380L, 100, KpiType.WPP_EARNING)
        assertStoryKpi(6420L, 100, KpiType.WPP_BONUS)
        assertStoryKpi(50200L, 200, KpiType.WPP_EARNING)
        assertStoryKpi(19280L, 200, KpiType.WPP_BONUS)
        assertStoryKpi(7430L, 201, KpiType.WPP_EARNING)
        assertStoryKpi(1490L, 201, KpiType.WPP_BONUS)
        assertStoryKpi(11150L, 202, KpiType.WPP_EARNING)
        assertStoryKpi(850L, 202, KpiType.WPP_BONUS)
        assertStoryKpi(28690L, 300, KpiType.WPP_EARNING)
        assertStoryKpi(0L, 300, KpiType.WPP_BONUS)
        assertStoryKpi(80L, 301, KpiType.WPP_EARNING)
        assertStoryKpi(0L, 301, KpiType.WPP_BONUS)

        assertFile(
            File("$storageDir/earnings/2020/01/wpp-user.csv"),
            """
                user_id,user_name,full_name,phone_number,earnings,bonus,total
                111,john111,Jane Doe,+237670000000,74380,6420,80800
                211,john200,Yo Man,+237670000001,68780,21620,90400
                311,john300,Ray,+237670000002,28770,0,28770
            """.trimIndent(),
        )
        assertUserKpi(74380L, 111, KpiType.WPP_EARNING)
        assertUserKpi(6420L, 111, KpiType.WPP_BONUS)
        assertUserKpi(68780L, 211, KpiType.WPP_EARNING)
        assertUserKpi(21620L, 211, KpiType.WPP_BONUS)
        assertUserKpi(28770, 311, KpiType.WPP_EARNING)
        assertUserKpi(0, 311, KpiType.WPP_BONUS)

        val budget = monthlyBudget.toLong()
        assertTrue(computeStoryTotal(KpiType.WPP_EARNING) + computeStoryTotal(KpiType.WPP_BONUS) <= budget)
        assertTrue(computeUserTotal(KpiType.WPP_EARNING) + computeUserTotal(KpiType.WPP_BONUS) <= budget)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        println("------------------------------")
        print(messages[0].content.toString())
    }

    private fun assertStoryKpi(expected: Long, storyId: Long, type: KpiType) {
        val now = DateUtils.toLocalDate(date).minusMonths(1)
        val kpi = storyKpiDao
            .findByStoryIdAndTypeAndYearAndMonthAndSource(
                storyId,
                type,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get()
        assertEquals(expected, kpi.value)
    }

    private fun computeUserTotal(type: KpiType): Long {
        val now = DateUtils.toLocalDate(date).minusMonths(1)
        return userKpiDao.findByTypeAndYearAndMonthAndSource(type, now.year, now.monthValue, TrafficSource.ALL)
            .sumOf { kpi -> kpi.value }
    }

    private fun assertUserKpi(expected: Long, userId: Long, type: KpiType) {
        val now = DateUtils.toLocalDate(date).minusMonths(1)
        val kpi = userKpiDao
            .findByUserIdAndTypeAndYearAndMonthAndSource(
                userId,
                type,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get()
        assertEquals(expected, kpi.value)
    }

    private fun computeStoryTotal(type: KpiType): Long {
        val now = DateUtils.toLocalDate(date).minusMonths(1)
        return storyKpiDao.findByTypeAndYearAndMonthAndSource(type, now.year, now.monthValue, TrafficSource.ALL)
            .sumOf { kpi -> kpi.value }
    }

    private fun assertFile(file: File, content: String) {
        assertTrue(file.exists())
        assertEquals(content, IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent())
    }
}
