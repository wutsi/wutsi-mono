package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.CategoryService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
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
    private val categoryService: CategoryService,

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

    private fun recommendWriters(blog: UserModel, user: UserModel): List<UserModel> {
        try {
            // Categories
            val categories = categoryService.search(
                SearchCategoryRequest(
                    level = 0,
                )
            )

            // User to exclude
            val subscribedIds = subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = user.id,
                    limit = 100,
                ),
            ).map { it.userId }
            val excludeUserIds = subscribedIds.toMutableList()
            excludeUserIds.add(user.id)
            excludeUserIds.add(blog.id)

            // Recommendation of writers to subscribe
            val language = user.language?.ifEmpty { null } ?: LocaleContextHolder.getLocale().language
            val users = userService.search(
                SearchUserRequest(
                    active = true,
                    excludeUserIds = excludeUserIds,
                    categoryIds = categories.map { it.id },
                    minPublishStoryCount = 2,
                    sortBy = UserSortStrategy.LAST_PUBLICATION,
                    sortOrder = SortOrder.DESCENDING,
                    limit = 100,
                    languages = listOf(language)
                )
            )
            val xusers = users.filter {
                it.hasPicture && // Has a picture
                        !it.biography.isNullOrEmpty() // Has description
            }
                .groupBy { it.categoryId ?: -1L }

            return xusers
                .map { it.value.shuffled().first() }
                .shuffled()
                .take(5)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to recommend stories", ex)
            return emptyList()
        }
    }
}
