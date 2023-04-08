package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.web.Fixtures
import com.wutsi.application.web.Page
import com.wutsi.application.web.service.recaptcha.Recaptcha
import com.wutsi.checkout.manager.dto.CreateOrderItemRequest
import com.wutsi.checkout.manager.dto.CreateOrderRequest
import com.wutsi.checkout.manager.dto.CreateOrderResponse
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.GetOrderResponse
import com.wutsi.checkout.manager.dto.SearchPaymentProviderResponse
import com.wutsi.enums.ChannelType
import com.wutsi.enums.DeviceType
import com.wutsi.enums.OrderType
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID

internal class DonateControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var recaptcha: Recaptcha

    private val orderId = UUID.randomUUID().toString()

    private val order = Fixtures.createOrder(id = orderId, businessId = merchant.businessId!!, accountId = merchant.id)

    private val mtn = Fixtures.createPaymentProviderSummary(1, "MTN")
    private val orange = Fixtures.createPaymentProviderSummary(2, "Orange")

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetMemberResponse(merchant)).whenever(membershipManagerApi).getMember(any())

        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        doReturn(CreateOrderResponse(orderId)).whenever(checkoutManagerApi).createOrder(any())
        doReturn(GetOrderResponse(order)).whenever(checkoutManagerApi).getOrder(orderId)

        doReturn(SearchPaymentProviderResponse(listOf(mtn, orange))).whenever(checkoutManagerApi)
            .searchPaymentProvider(any())

        doReturn(true).whenever(recaptcha).verify(any())
    }

    @Test
    fun `submit donation`() {
        // Goto order page
        navigate(url("u/${merchant.id}/donate"))

        assertCurrentPageIs(Page.DONATE)
        assertElementAttribute("head title", "text", "${merchant.displayName} - Donate | Wutsi")
        assertElementNotPresent(".error")

        // Enter data
        input("input[name=displayName]", "Ray Sponsible")
        input("input[name=email]", "ray.sponsible@gmail.com")
        input("input[name=confirm]", "ray.sponsible@gmail.com")
        input("textarea[name=notes]", "Merci")

        // Submit the data
        scrollToBottom()
        Thread.sleep(1000)
        assertElementAttribute("#btn-submit", "wutsi-track-event", "donate")
        click("#btn-submit")

        verify(checkoutManagerApi).createOrder(
            CreateOrderRequest(
                type = OrderType.DONATION.name,
                deviceType = DeviceType.DESKTOP.name,
                channelType = ChannelType.WEB.name,
                businessId = merchant.businessId!!,
                notes = "Merci",
                customerEmail = "ray.sponsible@gmail.com",
                customerName = "Ray Sponsible",
                items = listOf(
                    CreateOrderItemRequest(
                        productId = -1,
                        quantity = 1,
                    ),
                ),
            ),
        )

        // Check payment page
        assertCurrentPageIs(Page.PAYMENT)
    }

    @Test
    fun `submit donation from home`() {
        // Goto order page
        navigate(url("u/${merchant.id}/donate?dn=Ray&n=Merci"))

        assertCurrentPageIs(Page.DONATE)
        assertElementNotPresent(".error")
        assertElementAttribute("input[name=displayName]", "value", "Ray")
        assertElementText("textarea[name=notes]", "Merci")

        // Enter data
        input("input[name=email]", "ray.sponsible@gmail.com")
        input("input[name=confirm]", "ray.sponsible@gmail.com")

        // Submit the data
        scrollToBottom()
        Thread.sleep(1000)
        assertElementAttribute("#btn-submit", "wutsi-track-event", "donate")
        click("#btn-submit")

        verify(checkoutManagerApi).createOrder(
            CreateOrderRequest(
                type = OrderType.DONATION.name,
                deviceType = DeviceType.DESKTOP.name,
                channelType = ChannelType.WEB.name,
                businessId = merchant.businessId!!,
                notes = "Merci",
                customerEmail = "ray.sponsible@gmail.com",
                customerName = "Ray",
                items = listOf(
                    CreateOrderItemRequest(
                        productId = -1,
                        quantity = 1,
                    ),
                ),
            ),
        )

        // Check payment page
        assertCurrentPageIs(Page.PAYMENT)
    }

    @Test
    fun `recaptcha error`() {
        // Given
        doReturn(false).whenever(recaptcha).verify(any())

        // Goto order page
        navigate(url("u/${merchant.id}/donate"))
        assertCurrentPageIs(Page.DONATE)

        // Enter data
        input("input[name=displayName]", "Ray Sponsible")
        input("input[name=email]", "ray.sponsible@gmail.com")
        input("input[name=confirm]", "ray.sponsible@gmail.com")
        input("textarea[name=notes]", "This is a note :-)")

        // Submit the data
        scrollToBottom()
        Thread.sleep(1000)
        click("#btn-submit")

        verify(checkoutManagerApi, never()).createOrder(any())

        // Check payment page
        assertCurrentPageIs(Page.DONATE)
        assertElementPresent(".error")
    }
}
