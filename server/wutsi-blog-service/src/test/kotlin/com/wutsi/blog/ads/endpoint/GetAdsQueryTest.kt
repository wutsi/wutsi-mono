package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.GetAdsResponse
import com.wutsi.blog.util.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/GetAdsQuery.sql"])
class GetAdsQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate


    @Value("\${wutsi.application.ads.daily-budget.box}")
    private lateinit var dailyBudgetBox: java.lang.Long

    @Test
    fun get() {
        val response = rest.getForEntity("/v1/ads/100", GetAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val ads = response.body!!.ads
        assertEquals(AdsStatus.PUBLISHED, ads.status)
        assertEquals(AdsType.BOX, ads.type)
        assertEquals(AdsCTAType.BUY_NOW, ads.ctaType)
        assertEquals(100L, ads.userId)
        assertEquals("ads 100", ads.title)
        assertEquals("https://www.img.com/1.png", ads.imageUrl)
        assertEquals("https://www.google.ca", ads.url)
        assertNotNull(ads.startDate)
        assertEquals(DateUtils.addDays(ads.startDate!!, 5), ads.endDate)
        assertNotNull(ads.completedDateTime)
        assertEquals(1000L, ads.budget)
        assertEquals(dailyBudgetBox.toLong(), ads.dailyBudget)
        assertEquals(6, ads.durationDays)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/ads/999999", GetAdsResponse::class.java)
        assertEquals(404, response.statusCode.value())
    }
}