package com.wutsi.blog.app.page.admin.ads

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsSummary
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsResponse
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class AdsCampaignsControllerTest : SeleniumTestSupport() {
    companion object {
        private val USER_ID = 111L
    }

    private lateinit var user: User

    private val ads = listOf(
        AdsSummary(
            id = "1111",
            title = "Ads 1111",
            url = "https://www.google.ca",
            durationDays = 4,
            startDate = DateUtils.addDays(Date(), 1),
            endDate = DateUtils.addDays(Date(), 5),
            status = AdsStatus.DRAFT,
            budget = 150000,
            ctaType = AdsCTAType.BUY_NOW,
            currency = "XAF",
            imageUrl = "https://picsum.photos/300/600",
            type = AdsType.BOX_2X
        ),
        AdsSummary(
            id = "2222",
            title = "Ads 2222",
            url = "https://www.google.ca",
            durationDays = 4,
            startDate = DateUtils.addDays(Date(), 1),
            endDate = DateUtils.addDays(Date(), 5),
            status = AdsStatus.PUBLISHED,
            budget = 150000,
            ctaType = AdsCTAType.CONTACT_US,
            currency = "XAF",
            imageUrl = "https://picsum.photos/300/300",
            type = AdsType.BOX
        ),
        AdsSummary(
            id = "3333",
            title = "Ads 3333",
            url = "https://www.google.ca",
            durationDays = 4,
            startDate = DateUtils.addDays(Date(), -1),
            endDate = DateUtils.addDays(Date(), 5),
            status = AdsStatus.RUNNING,
            budget = 150000,
            ctaType = AdsCTAType.CONTACT_US,
            currency = "XAF",
            imageUrl = "https://picsum.photos/728/100",
            type = AdsType.BANNER_WEB,
            transactionId = "123232",
            totalClicks = 100,
            totalImpressions = 430909,
        ),
        AdsSummary(
            id = "4444",
            title = "Ads 4444",
            url = "https://www.google.ca",
            durationDays = 4,
            startDate = DateUtils.addDays(Date(), -10),
            endDate = DateUtils.addDays(Date(), -1),
            status = AdsStatus.COMPLETED,
            budget = 150000,
            ctaType = AdsCTAType.CONTACT_US,
            currency = "XAF",
            imageUrl = "https://picsum.photos/300/50",
            type = AdsType.BANNER_MOBILE,
            transactionId = "1232333",
            totalClicks = 300,
            totalImpressions = 420909,
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        user = setupLoggedInUser(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(user.name)
    }

    @Test
    fun test() {
        doReturn(SearchAdsResponse(ads)).whenever(adsBackend).search(any())

        navigate(url("/me/ads/campaigns"))
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS)

        assertElementCount(".ads", 4)

        assertElementPresent("#ad-" + ads[0].id + " .badge-draft")
        assertElementNotPresent("#ad-" + ads[0].id + " .btn-proceed-payment")
        assertElementNotPresent("#ad-" + ads[0].id + " .progress")

        assertElementPresent("#ad-" + ads[1].id + " .badge-published")
        assertElementPresent("#ad-" + ads[1].id + " .btn-proceed-payment")
        assertElementNotPresent("#ad-" + ads[1].id + " .progress")

        assertElementPresent("#ad-" + ads[2].id + " .badge-running")
        assertElementNotPresent("#ad-" + ads[2].id + " .btn-proceed-payment")
        assertElementPresent("#ad-" + ads[2].id + " .progress")

        assertElementPresent("#ad-" + ads[3].id + " .badge-completed")
        assertElementNotPresent("#ad-" + ads[3].id + " .btn-proceed-payment")
        assertElementNotPresent("#ad-" + ads[3].id + " .progress")

        click(".btn-create")
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_CREATE)
    }
}