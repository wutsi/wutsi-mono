package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SubscribedController(
    private val subscriptionService: SubscriptionService,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT: Int = 20
        private val LOGGER = LoggerFactory.getLogger(SubscribedController::class.java)
    }

    override fun pageName() = PageName.SUBSCRIBED

    @GetMapping("/subscribed")
    fun subscribed(
        @RequestParam(name = "writer-id", required = false) writerIds: List<Long>? = null,
        @RequestParam(name = "storyId-id", required = false) storyId: Long? = null,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
    ): String {
        writerIds?.let { ids ->
            try {
                ids.forEach { writerId ->
                    subscriptionService.subscribeTo(
                        writerId,
                        storyId,
                        storyId?.let { "story" } ?: "blog"
                    )
                }
            } catch (ex: Exception) {
                LOGGER.warn("Unable to subscribe", ex)
            }
        }

        return if (returnUrl == null) {
            "redirect:/"
        } else {
            "redirect:$returnUrl"
        }
    }
}
