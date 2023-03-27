package com.wutsi.application.marketplace.settings.discount.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.SearchDiscountResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SettingsV2DiscountListScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var clock: Clock

    private fun url() = "http://localhost:$port${Page.getSettingsDiscountListUrl()}"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val now = OffsetDateTime.of(2023, 1, 3, 0, 0, 0, 0, ZoneOffset.UTC)
        val discounts = listOf(
            Fixtures.createDiscountSummary(
                id = 1,
                starts = now.minusDays(2),
                ends = now.plusDays(10),
            ),
            Fixtures.createDiscountSummary(
                id = 2,
                starts = now.minusMonths(1),
                ends = now.minusMonths(1).plusDays(5),
            ),
            Fixtures.createDiscountSummary(
                id = 2,
                starts = now.plusDays(10),
                ends = now.plusDays(20),
            ),
        )
        doReturn(SearchDiscountResponse(discounts)).whenever(marketplaceManagerApi).searchDiscount(any())
    }

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/discount/screens/list.json", url())
}
