package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.SortOrder
import com.wutsi.blog.ads.dto.AdsSortStrategy
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.SearchAdsResponse
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql
import java.util.Date

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/SearchAdsQuery.sql"])
class SearchAdsQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun `by user-id`() {
        val request = SearchAdsRequest(
            userId = 100,
            sortBy = AdsSortStrategy.CREATED,
            sortOrder = SortOrder.DESCENDING
        )
        val response = rest.postForEntity("/v1/ads/queries/search", request, SearchAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val ads = response.body!!.ads
        assertEquals(2, ads.size)
    }

    @Test
    fun `by status`() {
        val request = SearchAdsRequest(
            status = listOf(AdsStatus.DRAFT, AdsStatus.COMPLETED),
            sortBy = AdsSortStrategy.TITLE,
        )
        val response = rest.postForEntity("/v1/ads/queries/search", request, SearchAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val ads = response.body!!.ads
        assertEquals(3, ads.size)
    }

    @Test
    fun `by start date`() {
        val request = SearchAdsRequest(
            startDateFrom = DateUtils.addDays(Date(), -1),
            startDateTo = DateUtils.addDays(Date(), 100),
            sortBy = AdsSortStrategy.START_DATE,
        )
        val response = rest.postForEntity("/v1/ads/queries/search", request, SearchAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val ads = response.body!!.ads
        assertEquals(2, ads.size)
    }

    @Test
    fun `by type`() {
        val request = SearchAdsRequest(
            type = listOf(AdsType.BANNER_HORIZONTAL),
        )
        val response = rest.postForEntity("/v1/ads/queries/search", request, SearchAdsResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val ads = response.body!!.ads
        assertEquals(3, ads.size)
    }
}