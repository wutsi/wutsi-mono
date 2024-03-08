package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.tracking.manager.dto.PushTrackRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
class ClickController(
    private val trackingBackend: TrackingBackend,
    private val tracingContext: TracingContext,
    private val requestContext: RequestContext,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClickController::class.java)
    }

    @GetMapping("/wclick")
    fun click(
        @RequestParam url: String,
        @RequestParam(name = "story-id", required = false) storyId: String? = null,
        @RequestParam(name = "ads-id", required = false) adsId: String? = null,
        @RequestParam(name = "hit-id", required = false) hitId: String? = null,
        @RequestParam(name = "blog-id", required = false) blogId: String? = null,
        @RequestParam(name = "page", required = false) page: String? = null,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        /* Push event */

        try {
            trackingBackend.push(
                PushTrackRequest(
                    time = System.currentTimeMillis(),
                    correlationId = hitId ?: UUID.randomUUID().toString(),
                    page = storyId?.let { PageName.READ } ?: page,
                    productId = storyId,
                    referrer = request.getHeader(HttpHeaders.REFERER),
                    event = "click",
                    ua = request.getHeader(HttpHeaders.USER_AGENT),
                    value = url,
                    deviceId = tracingContext.deviceId(),
                    accountId = requestContext.currentUser()?.id?.toString(),
                    campaign = adsId,
                    businessId = blogId,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        }

        /* Redirect */
        response.sendRedirect(url)
    }
}
