package com.wutsi.blog.app.page.admin.ads

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dto.Ads
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.CreateAdsResponse
import com.wutsi.blog.ads.dto.GetAdsResponse
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.platform.payment.core.Status
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class ViewAdsCampaignsControllerTest : SeleniumTestSupport() {
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
        url = "https://www.google.com",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        user = setupLoggedInUser(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(user.name)
    }

    @Test
    fun draft() {
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        doReturn(CreateAdsResponse(adsId = ads.id)).whenever(adsBackend).create(any())
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
        assertElementPresent("#btn-publish")
        assertElementNotPresent("#btn-proceed-payment")
        assertElementAttributeNull("#setting-section-type button", "disabled")
        assertElementAttributeNull("#setting-section-start-date button", "disabled")
        assertElementAttributeNull("#setting-section-end-date button", "disabled")

        selectValue("type", 2)
        input("title", ads.title, "This is a new title")
        input("url", ads.url, "https://www.google.com")
        scrollToMiddle()
        selectValue("cta_type", 1)
        selectValue("country", 4)
        selectValue("language", 5)
        selectValue("os", 1)
        selectValue("email", 1)
        verify(adsBackend, times(8)).updateAttribute(any())
    }

    @Test
    fun running() {
        doReturn(GetAdsResponse(ads.copy(status = AdsStatus.RUNNING))).whenever(adsBackend).get(any())

        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        doReturn(CreateAdsResponse(adsId = ads.id)).whenever(adsBackend).create(any())
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-proceed-payment")
        assertElementAttribute("#setting-section-type button", "disabled", "true")
        assertElementAttribute("#setting-section-start-date button", "disabled", "true")
        assertElementAttribute("#setting-section-end-date button", "disabled", "true")
    }

    @Test
    fun completed() {
        doReturn(GetAdsResponse(ads.copy(status = AdsStatus.COMPLETED))).whenever(adsBackend).get(any())

        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        doReturn(CreateAdsResponse(adsId = ads.id)).whenever(adsBackend).create(any())
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-proceed-payment")
        assertElementAttribute(".setting-section button", "disabled", "true")
    }

    @Test
    fun `published not paid`() {
        doReturn(GetAdsResponse(ads.copy(status = AdsStatus.PUBLISHED))).whenever(adsBackend).get(any())

        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        doReturn(CreateAdsResponse(adsId = ads.id)).whenever(adsBackend).create(any())
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
        assertElementNotPresent("#btn-publish")
        assertElementPresent("#btn-proceed-payment")
        assertElementAttribute("#setting-section-type button", "disabled", "true")
        assertElementAttribute("#setting-section-start-date button", "disabled", "true")
        assertElementAttribute("#setting-section-end-date button", "disabled", "true")
    }

    @Test
    fun `published and paid`() {
        val tx = Transaction(
            id = "4304309",
            adsId = ads.id,
            status = Status.SUCCESSFUL,
            creationDateTime = Date(),
            email = "ray.sponsible@gmail.com",
            paymentMethodNumber = "+2367700000000",
            paymentMethodOwner = "Ray Sponsible"
        )
        doReturn(
            GetTransactionResponse(tx)
        ).whenever(transactionBackend).get(any(), anyOrNull())

        doReturn(
            GetAdsResponse(
                ads.copy(
                    status = AdsStatus.PUBLISHED,
                    transactionId = tx.id
                )
            )
        ).whenever(adsBackend).get(any())

        navigate(url("/me/ads/campaigns/${ads.id}"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)

        doReturn(CreateAdsResponse(adsId = ads.id)).whenever(adsBackend).create(any())
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
        assertElementAttribute("#setting-section-type button", "disabled", "true")
        assertElementAttribute("#setting-section-start-date button", "disabled", "true")
        assertElementAttribute("#setting-section-end-date button", "disabled", "true")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-proceed-payment")
    }

    private fun input(
        name: String,
        originalValue: String?,
        newValue: String,
        error: String? = null,
    ) {
        val selector = "#$name-form"

        // Test current value
        assertElementAttribute("$selector .form-control", "value", originalValue ?: "")

        // Change
        click("$selector .btn-edit")
        input("$selector .form-control", newValue)
        click("$selector .btn-save", 1000)

        // Verify changes
        assertElementAttribute("$selector .form-control", "value", newValue)
        if (error == null) {
            assertElementHasClass("$selector .alert-danger", "hidden")
        } else {
            assertElementHasNotClass("$selector .alert-danger", "hidden")
            assertElementPresent("$selector .alert-danger")
        }
    }

    private fun selectValue(name: String, newValue: Int) {
        val selector = "#$name-form"

        click("$selector .btn-edit")
        select("$selector .form-control", newValue)
        click("$selector .btn-save", 1000)
    }
}