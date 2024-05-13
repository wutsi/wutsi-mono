package com.wutsi.blog.app.page.admin.ads

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dto.Ads
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.Gender
import com.wutsi.blog.ads.dto.GetAdsResponse
import com.wutsi.blog.ads.dto.OS
import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitPaymentCommand
import com.wutsi.blog.transaction.dto.SubmitPaymentResponse
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.test.assertNull

class PayAdsControllerTest : SeleniumTestSupport() {
    companion object {
        private val USER_ID = 111L
    }

    private lateinit var user: User
    private val ads = Ads(
        id = "1111",
        title = "Ads 1111",
        durationDays = 4,
        startDate = DateUtils.addDays(Date(), 1),
        endDate = DateUtils.addDays(Date(), 5),
        status = AdsStatus.DRAFT,
        budget = 150000,
        ctaType = AdsCTAType.BUY_NOW,
        currency = "XAF",
        imageUrl = "https://picsum.photos/300/600",
        type = AdsType.BOX_2X,
        userId = USER_ID,
        totalImpressions = 1430943,
        totalClicks = 3435,
        email = true,
        url = "https://www.google.com",
        language = "fr",
        country = "FR",
        gender = Gender.FEMALE,
        os = OS.ANDROID
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        user = setupLoggedInUser(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(user.name)

        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())
    }

    @Test
    fun success() {
        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        click("#btn-publish")
        driver.switchTo().alert().accept()

        Thread.sleep(1000)
        val request = argumentCaptor<PublishAdsCommand>()
        verify(adsBackend).publish(request.capture())
        assertEquals(ads.id, request.firstValue.id)

        assertCurrentPageIs(PageName.ADS_PAY)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")

        val transactionId = "1223"
        doReturn(
            SubmitPaymentResponse(transactionId = transactionId, status = Status.PENDING.name),
        ).whenever(transactionBackend).pay(any())

        doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    id = transactionId,
                    status = Status.PENDING,
                    type = TransactionType.PAYMENT,
                    adsId = ads.id
                )
            ),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    id = transactionId,
                    status = Status.SUCCESSFUL,
                    type = TransactionType.CHARGE,
                )
            ),
        ).whenever(transactionBackend).get(any(), any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.PROCESSING)

        val cmd = argumentCaptor<SubmitPaymentCommand>()
        verify(transactionBackend).pay(cmd.capture())
        assertEquals(ads.id, cmd.firstValue.adsId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(ads.budget, cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)
        assertEquals(USER_ID, cmd.firstValue.userId)
        assertNull(cmd.firstValue.internationalCurrency)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(15000)
        assertElementNotVisible("#processing-container")
        assertElementVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")
        assertElementNotPresent("#btn-download")

        click("#btn-continue")
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
    }

    @Test
    fun failed() {
        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        click("#btn-publish")
        driver.switchTo().alert().accept()

        Thread.sleep(1000)
        val request = argumentCaptor<PublishAdsCommand>()
        verify(adsBackend).publish(request.capture())
        assertEquals(ads.id, request.firstValue.id)

        assertCurrentPageIs(PageName.ADS_PAY)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")

        val transactionId = "1223"
        doReturn(
            SubmitPaymentResponse(transactionId = transactionId, status = Status.PENDING.name),
        ).whenever(transactionBackend).pay(any())

        doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    id = transactionId,
                    status = Status.PENDING,
                    type = TransactionType.PAYMENT,
                    adsId = ads.id
                )
            ),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    id = transactionId,
                    status = Status.FAILED,
                    type = TransactionType.CHARGE,
                    errorCode = ErrorCode.FRAUDULENT.name,
                )
            ),
        ).whenever(transactionBackend).get(any(), any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.PROCESSING)

        val cmd = argumentCaptor<SubmitPaymentCommand>()
        verify(transactionBackend).pay(cmd.capture())
        assertEquals(ads.id, cmd.firstValue.adsId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(ads.budget, cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)
        assertEquals(USER_ID, cmd.firstValue.userId)
        assertNull(cmd.firstValue.internationalCurrency)

        Thread.sleep(15000)
        assertElementNotVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementVisible("#failed-container")
        assertElementNotVisible("#expired-container")
        assertElementNotPresent("#btn-download")

        click("#btn-try-again")
        assertCurrentPageIs(PageName.ADS_PAY)
    }
}