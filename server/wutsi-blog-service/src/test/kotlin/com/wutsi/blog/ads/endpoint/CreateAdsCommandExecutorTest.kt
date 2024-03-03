package com.wutsi.blog.ads.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.CreateAdsResponse
import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.util.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/CreateAdsCommand.sql"])
class CreateAdsCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: AdsRepository

    @MockBean
    private lateinit var clock: Clock

    private val now = SimpleDateFormat("yyyy-MM-dd").parse("2024-12-20")

    @BeforeEach
    fun setUp() {
        doReturn(now.time).whenever(clock).millis()
    }

    @Test
    fun execute() {
        val request = CreateAdsCommand(
            title = "product 111",
            userId = 100,
            type = AdsType.BANNER_MOBILE,
            currency = "XAF"
        )
        val response = rest.postForEntity("/v1/ads/commands/create", request, CreateAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val id = response.body!!.adsId
        val ads = dao.findById(id).get()

        assertEquals(request.type, ads.type)
        assertEquals(request.title, ads.title)
        assertEquals(request.type, ads.type)
        assertEquals(request.userId, ads.userId)
        assertEquals(request.currency, ads.currency)
        assertEquals(AdsStatus.DRAFT, ads.status)
        assertEquals(AdsCTAType.UNKNOWN, ads.ctaType)
        assertNotNull(ads.startDate)
        assertEquals(DateUtils.addDays(ads.startDate!!, AdsService.DEFAULT_DURATION.toInt()), ads.endDate)
    }
}