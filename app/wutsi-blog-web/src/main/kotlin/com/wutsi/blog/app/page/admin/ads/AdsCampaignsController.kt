package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.SortOrder
import com.wutsi.blog.ads.dto.AdsSortStrategy
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/ads/campaigns")
class AdsCampaignsController(
    private val service: AdsService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    override fun pageName() = PageName.ADS_CAMPAIGNS

    @GetMapping
    fun index(model: Model): String {
        val ads = service.search(
            SearchAdsRequest(
                userId = requestContext.currentUser()?.id ?: -1,
                limit = LIMIT,
                sortBy = AdsSortStrategy.START_DATE,
                sortOrder = SortOrder.DESCENDING
            )
        )
        model.addAttribute("ads", ads)
        return "admin/ads/campaigns"
    }
}
