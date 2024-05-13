package com.wutsi.blog.app.page.admin.ads

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dto.Ads
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.CreateAdsResponse
import com.wutsi.blog.ads.dto.GetAdsResponse
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class CreateAdsCampaignsControllerTest : SeleniumTestSupport() {
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
        type = AdsType.BOX_2X
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        user = setupLoggedInUser(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(USER_ID)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(user.name)
    }

    @Test
    fun create() {
        navigate(url("/me/ads/campaigns"))
        click(".btn-create")

        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_CREATE)

        input("#title", ads.title)

        doReturn(CreateAdsResponse(adsId = ads.id)).whenever(adsBackend).create(any())
        doReturn(GetAdsResponse(ads)).whenever(adsBackend).get(any())

        click("#btn-submit")
        assertCurrentPageIs(PageName.ADS_CAMPAIGNS_VIEW)
    }
}