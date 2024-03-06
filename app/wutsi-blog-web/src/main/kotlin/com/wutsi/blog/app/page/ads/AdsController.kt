package com.wutsi.blog.app.page.ads

import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.RequestContext
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class AdsController(
    private val service: AdsService,
    private val requestContext: RequestContext,
) {
    @GetMapping("/ads/banner")
    fun banner(
        @RequestParam(name = "blog-id", required = false) blogId: Long? = null,
        @RequestParam(required = false) type: String? = null,
        model: Model,
    ): String {
        if (!requestContext.isBot()) {
            loadBanner(type, blogId, model)
        }
        return "ads/banner"
    }

    private fun loadBanner(type: String?, blogId: Long?, model: Model) {
        val types = type
            ?.split(",")
            ?.mapNotNull { item ->
                try {
                    AdsType.valueOf(item.trim().uppercase())
                } catch (ex: Exception) {
                    null
                }
            }
            ?: emptyList()

        val banners = service.search(
            SearchAdsRequest(
                type = types,
                status = listOf(AdsStatus.RUNNING),
                limit = 20,
                impressionContext = AdsImpressionContext(
                    blogId = blogId,
                    userId = requestContext.currentUser()?.id,
                    userAgent = requestContext.request.getHeader("User-Agent"),
                    ip = requestContext.remoteIp(),
                    adsPerType = 5,
                )
            )
        )
        model.addAttribute("banner", banners.shuffled().firstOrNull())
    }
}
