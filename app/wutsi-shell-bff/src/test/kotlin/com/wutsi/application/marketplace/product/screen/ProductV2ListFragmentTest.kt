package com.wutsi.application.marketplace.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.SearchOfferResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProductV2ListFragmentTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var regulationEngine: RegulationEngine

    private fun url() = "http://localhost:$port${Page.getProductListUrl()}/fragment?id=111"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val products = listOf(
            Fixtures.createOfferSummary(Fixtures.createProductSummary(id = 1)),
            Fixtures.createOfferSummary(
                Fixtures.createProductSummary(
                    id = 2,
                    quantity = regulationEngine.lowStockThreshold() - 1,
                ),
            ),
            Fixtures.createOfferSummary(Fixtures.createProductSummary(id = 3, quantity = 0)),
            Fixtures.createOfferSummary(
                product = Fixtures.createProductSummary(id = 4, price = 2500),
                price = Fixtures.createOfferPrice(4, price = 2000, referencePrice = 2500),
            ),
            Fixtures.createOfferSummary(Fixtures.createProductSummary(id = 5)),
        )
        doReturn(SearchOfferResponse(products)).whenever(marketplaceManagerApi).searchOffer(any())
    }

    @Test
    fun products() {
        val member = Fixtures.createMember(business = true, storeId = 111)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        assertEndpointEquals("/marketplace/product/screens/list.json", url())
    }

    @Test
    fun notBusiness() {
        val member = Fixtures.createMember(business = false, storeId = 111)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        assertEndpointEquals("/marketplace/product/screens/list-not-business.json", url())
    }

    @Test
    fun noStore() {
        val member = Fixtures.createMember(business = true, storeId = null)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        assertEndpointEquals("/marketplace/product/screens/list-not-store.json", url())
    }
}
