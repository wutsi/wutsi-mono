package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.CreateAdsResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/CreateAdsCommand.sql"])
class CreateAdsCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: AdsRepository

    @Test
    fun execute() {
        val request = CreateAdsCommand(
            title = "product 111",
            userId = 100,
            type = AdsType.POST,
        )

        val response = rest.postForEntity("/v1/ads/commands/create", request, CreateAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val id = response.body!!.adsId
        val product = dao.findById(id).get()

        assertEquals(request.type, product.type)
        assertEquals(request.title, product.title)
        assertEquals(request.type, product.type)
        assertEquals(request.userId, product.userId)
        assertEquals(1, product.durationDays)
        assertEquals(AdsStatus.DRAFT, product.status)
        assertEquals(AdsCTAType.UNKNOWN, product.ctaType)
    }
}