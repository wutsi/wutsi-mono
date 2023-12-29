package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
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
        model: Model,
    ): String {
        // Subscribe
        val blog = userService.get(name)
        val storyId = extractIdFromSlug(returnUrl, "/read/")

        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))
        model.addAttribute("returnUrl", returnUrl)
        model.addAttribute("storyId", storyId)

        val user = requestContext.currentUser()
        if (user != null) {
            subscriptionService.subscribeTo(
                userId = blog.id,
                storyId = storyId,
                referer = resolveReferer(returnUrl)
            )

            // Writers to recommend
            val writers = recommendWriters(blog, user)
            if (writers.isNotEmpty()) {
                model.addAttribute("writers", writers)
            }
        }
        return "reader/subscribe"
    }

    private fun getPage(user: UserModel) = createPage(
        name = pageName(),
        title = user.fullName,
        description = user.biography ?: "",
        url = url(user),
        imageUrl = if (user.blog) {
            "$baseUrl/@/${user.name}/image.png"
        } else {
            null
        },
    )

    private fun recommendWriters(blog: UserModel, user: UserModel): List<UserModel> =
        try {
            // Subscription
            val subscribedIds = subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = user.id,
                    limit = 100,
                ),
            ).map { it.userId }

            // Recommendation of writers to subscribe
            val language = user.language ?: LocaleContextHolder.getLocale().language
            userService.trending(20 + subscribedIds.size + 1)
                .filter {
                    !subscribedIds.contains(it.id) && // Not a subscriber
                        it.id != user.id && // Not me
                        it.id != blog.id && // Not the blog to subscribe to
                        !it.pictureUrl.isNullOrEmpty() && // Has a picture
                        !it.biography.isNullOrEmpty() && // Has description
                        it.language == language && // Same language
                        it.subscriberCount >= WPPConfig.MIN_SUBSCRIBER_COUNT // Has enough subscribers
                }
                .shuffled()
                .take(5)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to recommend stories", ex)
            emptyList()
        }
}
