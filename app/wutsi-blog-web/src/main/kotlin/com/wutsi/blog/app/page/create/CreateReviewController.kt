package com.wutsi.blog.app.page.create

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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/create/review")
class CreateReviewController(
    private val subscriptionService: SubscriptionService,
    userService: UserService,
    requestContext: RequestContext,
) : AbstractCreateController(userService, requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreateReviewController::class.java)
    }

    override fun pageName() = PageName.CREATE_REVIEW
    override fun pagePath() = "create/review"
    override fun redirectUrl() = "/create/success"
    override fun attributeName() = ""
    override fun value(): String = ""

    override fun doSubmit(value: String?) {
        userService.createBlog(emptyList())
    }

    @GetMapping("/create-blog")
    fun submit(
        @RequestParam(name = "writerIds", required = false) writerIds: List<Long>? = null,
        model: Model,
    ): String {
        try {
            userService.createBlog(writerIds ?: emptyList())
            return "redirect:" + redirectUrl()
        } catch (ex: Exception) {
            val error = errorKey(ex)
            model.addAttribute("error", requestContext.getMessage(error))
            return pagePath()
        }
    }

    @GetMapping
    override fun index(model: Model): String {
        val user = requestContext.currentUser()
        if (user != null) {
            val writers = recommendWriters(user)
            if (writers.isNotEmpty()) {
                model.addAttribute("writers", writers)
            }
        }

        return super.index(model)
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
                        !it.pictureUrl.isNullOrEmpty() && // Has a picture
                        !it.biography.isNullOrEmpty() // Has a biography
                }
                .shuffled()
                .take(5)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to recommend stories", ex)
            emptyList()
        }
}
