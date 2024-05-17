package com.wutsi.blog.app.page.ads

import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.form.TrackForm
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping
class AdsController(
    private val service: AdsService,
    private val trackingBackend: TrackingBackend,
    private val requestContext: RequestContext,
    private val tracingContext: TracingContext,
) {
    @GetMapping("/ads/banner")
    fun banner(
        @RequestParam(name = "blog-id", required = false) blogId: Long? = null,
        @RequestParam(name = "category-id", required = false) categoryId: Long? = null,
        @RequestParam(required = false) type: String? = null,
        model: Model,
    ): String {
        if (!requestContext.isBot()) {
            loadBanner(blogId, categoryId, type, model)
        }
        return "ads/banner"
    }

    private fun loadBanner(blogId: Long?, categoryId: Long?, type: String?, model: Model) {
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
                    categoryId = categoryId
                )
            )
        )
        model.addAttribute("banner", banners.shuffled().firstOrNull())
    }

    @ResponseBody
    @PostMapping("/ads/track")
    fun track(
        @RequestBody form: TrackForm,
    ): Map<String, String> {
        val user = requestContext.currentUser()
        trackingBackend.push(
            PushTrackRequest(
                time = form.time,
                correlationId = form.hitId,
                productId = form.storyId,
                event = "impression",
                deviceId = tracingContext.deviceId(),
                url = form.url,
                ua = form.ua,
                value = form.value,
                page = form.page,
                referrer = form.referrer,
                accountId = user?.id?.toString(),
                ip = requestContext.remoteIp(),
                campaign = form.campaign,
                businessId = form.businessId,
            ),
        )

        return emptyMap()
    }
}
