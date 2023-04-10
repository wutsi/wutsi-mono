package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.web.Fixtures
import com.wutsi.application.web.Page
import com.wutsi.enums.ProductType
import com.wutsi.enums.StoreStatus
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.manager.dto.GetOfferResponse
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.time.ZoneId

internal class ProductControllerTest : SeleniumTestSupport() {
    @Autowired
    private lateinit var regulationEngine: RegulationEngine

    var product = Fixtures.createProduct(
        id = 11,
        storeId = merchant.storeId!!,
        accountId = merchant.id,
        pictures = listOf(
            Fixtures.createPictureSummary(1, "https://i.com/1.png"),
            Fixtures.createPictureSummary(2, "https://i.com/2.png"),
            Fixtures.createPictureSummary(3, "https://i.com/3.png"),
            Fixtures.createPictureSummary(4, "https://i.com/4.png"),
        ),
    )
    var offer = Fixtures.createOffer(product = product)

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(product.id)
    }

    @Test
    fun physicalProduct() {
        // Goto product page
        navigate(url("p/${product.id}/title-of-product"))
        assertCurrentPageIs(Page.PRODUCT)
        assertElementAttribute("head meta[name='wutsi\\:product_id']", "content", product.id.toString())
        assertElementAttribute("head meta[name='wutsi\\:business_id']", "content", merchant.businessId!!.toString())
        assertElementPresent("head meta[name='wutsi\\:hit_id']")

        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute("head meta[property='og:title']", "content", product.title)
        assertElementAttribute("head meta[property='og:description']", "content", product.summary)
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            product.thumbnail?.url,
        )
        assertElementAttributeContains(
            "head meta[property='og:url']",
            "content",
            "/p/${product.id}",
        )

        assertElementAttributeContains(
            "head link[rel='canonical']",
            "href",
            "/p/${product.id}",
        )

        assertElementCount("#picture-carousel .carousel-item", product.pictures.size)

        assertElementText(".product .title", product.title)
        assertElementText(".product .description", product.description!!)

        assertElementPresent(".product .price")
        assertElementNotPresent("#quantity-out-of-stock")
        assertElementNotPresent("#quantity-low-stock")
        assertElementPresent(".product [name='q']")
        assertElementAttribute(".product input[name='p']", "value", "${product.id}")
        assertElementPresent("#btn-buy")

        assertElementPresent("#button-facebook")
        assertElementPresent("#button-twitter")
        assertElementPresent("#button-instagram")
        assertElementPresent("#button-youtube")
        assertElementPresent("#button-website")
    }

    @Test
    fun event() {
        // Given
        offer = Fixtures.createOffer(
            product = product.copy(
                type = ProductType.EVENT.name,
                event = Fixtures.createEvent(
                    online = true,
                    meetingProvider = Fixtures.createMeetingProviderSummary(),
                ),
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(product.id)

        // Goto product page
        navigate(url("p/${product.id}"))
        assertCurrentPageIs(Page.PRODUCT)
        assertElementAttribute("head meta[name='wutsi\\:product_id']", "content", product.id.toString())
        assertElementPresent("head meta[name='wutsi\\:hit_id']")

        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute("head meta[property='og:title']", "content", product.title)
        assertElementAttribute("head meta[property='og:description']", "content", product.summary)
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            product.thumbnail?.url,
        )
        assertElementAttributeContains(
            "head meta[property='og:url']",
            "content",
            "/p/${product.id}",
        )

        assertElementCount("#picture-carousel .carousel-item", product.pictures.size)

        assertElementText(".product .title", product.title)
        assertElementText(".product .description", product.description!!)

        assertElementPresent(".product .price")
        assertElementNotPresent("#quantity-out-of-stock")
        assertElementNotPresent("#quantity-low-stock")
        assertElementAttribute(".product [name='q']", "value", "1")
        assertElementAttribute(".product [name='p']", "value", "${product.id}")
        assertElementPresent("#btn-buy")

        assertElementPresent("#product-delivery")
        assertElementPresent("#product-delivery-event-online")

        assertElementPresent("#button-facebook")
        assertElementPresent("#button-twitter")
        assertElementPresent("#button-instagram")
        assertElementPresent("#button-youtube")
    }

    @Test
    fun digitalDownload() {
        // Given
        offer = Fixtures.createOffer(
            product = product.copy(
                type = ProductType.DIGITAL_DOWNLOAD.name,
                files = listOf(
                    Fixtures.createFileSummary(1),
                    Fixtures.createFileSummary(2),
                    Fixtures.createFileSummary(3, "foo.pdf"),
                ),
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        // Goto product page
        navigate(url("p/${product.id}"))
        assertCurrentPageIs(Page.PRODUCT)
        assertElementAttribute("head meta[name='wutsi\\:product_id']", "content", product.id.toString())
        assertElementPresent("head meta[name='wutsi\\:hit_id']")

        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute("head meta[property='og:title']", "content", product.title)
        assertElementAttribute("head meta[property='og:description']", "content", product.summary)
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            product.thumbnail?.url,
        )
        assertElementAttributeContains(
            "head meta[property='og:url']",
            "content",
            "/p/${product.id}",
        )

        assertElementCount("#picture-carousel .carousel-item", product.pictures.size)

        assertElementText(".product .title", product.title)
        assertElementText(".product .description", product.description!!)

        assertElementText(".product .price", "1 500 FCFA")
        assertElementNotPresent(".product .reference-price")
        assertElementNotPresent(".product .discount-percent")

        assertElementAttribute(".product [name='q']", "value", "1")
        assertElementAttribute(".product [name='p']", "value", "${product.id}")
        assertElementNotPresent("#quantity-out-of-stock")
        assertElementNotPresent("#quantity-low-stock")
        assertElementPresent("#btn-buy")

        assertElementPresent("#product-delivery")
        assertElementPresent("#product-delivery-digital-download")
    }

    @Test
    fun `product with discount ends in more than 24h`() {
        // Given
        offer = Fixtures.createOffer(
            product = product,
            price = Fixtures.createOfferPrice(
                productId = product.id,
                referencePrice = 50000,
                price = 40000,
                savings = 10000,
                discountId = 11,
                expires = OffsetDateTime.now(ZoneId.of("UTC")).plusHours(40),
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        // Goto product page
        navigate(url("p/${product.id}"))

        assertCurrentPageIs(Page.PRODUCT)
        assertElementText(".product .price", "40 000 FCFA")
        assertElementText(".product .reference-price", "50 000 FCFA")
        assertElementText(".product .discount-percent", "20%")
        assertElementNotPresent(".product .urgency")
        assertElementNotPresent(".product .urgency-countdown")
    }

    @Test
    fun `product with discount ends in less than 24h`() {
        // Given
        offer = Fixtures.createOffer(
            product = product,
            price = Fixtures.createOfferPrice(
                productId = product.id,
                referencePrice = 50000,
                price = 40000,
                savings = 10000,
                discountId = 11,
                expires = OffsetDateTime.now(ZoneId.of("UTC")).plusHours(23),
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        // Goto product page
        navigate(url("p/${product.id}"))

        assertCurrentPageIs(Page.PRODUCT)
        assertElementText(".product .price", "40 000 FCFA")
        assertElementText(".product .reference-price", "50 000 FCFA")
        assertElementText(".product .discount-percent", "20%")
        assertElementPresent(".product .urgency")
        assertElementNotPresent(".product .urgency-countdown")
    }

    @Test
    fun `product with discount ends in less than 1h`() {
        // Given
        offer = Fixtures.createOffer(
            product = product,
            price = Fixtures.createOfferPrice(
                productId = product.id,
                referencePrice = 50000,
                price = 40000,
                savings = 10000,
                discountId = 11,
                expires = OffsetDateTime.now(ZoneId.of("UTC")).plusMinutes(35),
            ),
        )
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        // Goto product page
        navigate(url("p/${product.id}"))

        assertCurrentPageIs(Page.PRODUCT)
        assertElementText(".product .price", "40 000 FCFA")
        assertElementText(".product .reference-price", "50 000 FCFA")
        assertElementText(".product .discount-percent", "20%")
//        assertElementPresent(".product .urgency")
//        assertElementPresent(".product .urgency-countdown script")
    }

    @Test
    fun `product out-of-stock`() {
        // Given
        val offer = offer.copy(product = product.copy(quantity = 0))
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        // Goto product page
        navigate(url("p/${product.id}"))

        assertCurrentPageIs(Page.PRODUCT)
        assertElementPresent("#quantity-out-of-stock")
        assertElementNotPresent("#quantity-low-stock")
        assertElementNotPresent(".product [name='q']")
        assertElementNotPresent(".product [name='p']")
        assertElementNotPresent("#btn-buy")
    }

    @Test
    fun `product low-stock`() {
        // Given
        val product = Fixtures.createProduct(
            id = 11,
            storeId = merchant.storeId!!,
            accountId = merchant.id,
            pictures = listOf(
                Fixtures.createPictureSummary(1, "https://i.com/1.png"),
            ),
            quantity = 0,
        )
        val offer = Fixtures.createOffer(product = product)
        doReturn(GetOfferResponse(offer)).whenever(marketplaceManagerApi).getOffer(any())

        // Goto product page
        navigate(url("p/${product.id}"))

        assertCurrentPageIs(Page.PRODUCT)
        assertElementNotPresent("#quantity-out-of-stock")
        assertElementPresent("#quantity-low-stock")
        assertElementPresent(".product [name='q']")
        assertElementPresent(".product [name='p']")
        assertElementPresent("#btn-buy")
    }

    @Test
    fun productNotFound() {
        val ex = createFeignNotFoundException(errorCode = ErrorURN.PRODUCT_NOT_FOUND.urn)
        doThrow(ex).whenever(marketplaceManagerApi).getOffer(any())

        navigate(url("p/99999"))
        assertCurrentPageIs(Page.ERROR)
    }

    @Test
    fun accountWithoutStore() {
        merchant = merchant.copy(storeId = null)
        doReturn(GetMemberResponse(merchant)).whenever(membershipManagerApi).getMember(any())

        navigate(url("p/1111"))
        assertCurrentPageIs(Page.ERROR)
    }

    @Test
    fun storeNotActive() {
        store = store.copy(status = StoreStatus.INACTIVE.name)
        doReturn(GetStoreResponse(store)).whenever(marketplaceManagerApi).getStore(any())

        navigate(url("p/111"))
        assertCurrentPageIs(Page.ERROR)
    }
}
