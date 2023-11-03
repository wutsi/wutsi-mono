package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SubscribeController(
    private val userService: UserService,
    private val subscriptionService: SubscriptionService,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT: Int = 20
        private val LOGGER = LoggerFactory.getLogger(SubscribeController::class.java)
    }

    override fun pageName() = PageName.SUBSCRIBE

    @GetMapping("/@/{name}/subscribe")
    fun subscribe(
        @PathVariable name: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "story-id", required = false) storyId: Long? = null,
        model: Model,
    ): String {
        // Subscribe
        val blog = userService.get(name)
        subscriptionService.subscribeTo(
            blog.id,
            storyId,
            storyId?.let { "story" } ?: "blog"
        )

        // Writers to recommend
        val user = requestContext.currentUser()
        val writers = recommendWriters(user!!)
        if (writers.isNotEmpty()) {
            model.addAttribute("writers", writers)
        }

        model.addAttribute("blog", blog)
        model.addAttribute("returnUrl", returnUrl)
        model.addAttribute("storyId", storyId)
        return "reader/subscribe"
    }

    private fun recommendWriters(user: UserModel): List<UserModel> =
        try {
            // Subscription
            val subscribedIds = subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = user.id,
                    limit = 100,
                ),
            ).map { it.userId }

            // Recommendation of writers to subscribe
            userService.recommend(20 + subscribedIds.size)
                .filter {
                    !subscribedIds.contains(it.id) && // Not a subscriber
                        it.id != user.id && // Not me
                        !it.pictureUrl.isNullOrEmpty() // Has a picture
                }
                .shuffled()
                .take(5)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to recommend stories", ex)
            emptyList()
        }
}
