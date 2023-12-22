package com.wutsi.blog.earning.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.earning.service.WPPEarningService
import com.wutsi.blog.mail.service.SMTPSender
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/earning/WPPEarningCalculatorJob.sql"])
class WPPDailyEarningCalculatorJobTest {
    @Autowired
    private lateinit var job: WPPDailyEarningCalculatorJob

    @MockBean
    private lateinit var service: WPPEarningService

    @MockBean
    private lateinit var smtp: SMTPSender

    @MockBean
    private lateinit var clock: Clock

    private val date = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-20")

    @BeforeEach
    fun setUp() {
        doReturn(date.time).whenever(clock).millis()

        val date = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-20")
        doReturn(date.time).whenever(clock).millis()
    }

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        verify(service).compile(2020, 2, 200000 * 20 / 29)
        verify(smtp, never()).send(any())
    }
}
