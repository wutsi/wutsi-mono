package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.reader.view.StoryRssView
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Controller
@RequestMapping("/")
class HomeController(
    private val schemas: WutsiSchemasGenerator,
    private val userService: UserService,
    private val storyService: StoryService,
    private val subscriptionService: SubscriptionService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeController::class.java)
        private val LIMIT = 20
    }

    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun page() = createPage(
        title = "Wutsi - " + requestContext.getMessage("wutsi.slogan"),
        description = requestContext.getMessage("wutsi.tagline"),
        schemas = schemas.generate(),
        rssUrl = "/rss",
    )

    @GetMapping
    fun index(model: Model): String {
        val user = requestContext.currentUser()
        if (user == null) {
            return anonymous(model)
        } else {
            return recommended(model)
        }
    }

    private fun anonymous(model: Model): String {
        val writers = findWriters(null, emptyList())
        model.addAttribute("writers", writers)
        return "reader/home"
    }

    private fun recommended(model: Model): String {
        val stories = recommend().map { it.copy(slug = "${it.slug}?referer=for-you") }
        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories)
        }
        model.addAttribute("tab", "recommended")
        return "reader/home_authenticated"
    }

    @GetMapping("/following")
    fun following(model: Model): String {
        following(0, model)
        model.addAttribute("tab", "following")
        return "reader/home_authenticated"
    }

    @GetMapping("/following/stories")
    fun following(@RequestParam offset: Int, model: Model): String {
        val user = requestContext.currentUser()
            ?: return "reader/fragment/home-stories"

        // Subscriptions
        val subscriptions = findSubscriptions(user)

        // Stories
        val stories = findStories(subscriptions, offset).map { it.copy(slug = "${it.slug}?referer=following") }

        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories)
            if (stories.size >= LIMIT) {
                model.addAttribute("moreUrl", "/following/stories?offset=" + (LIMIT + offset))
            }
        }
        return "reader/fragment/home-stories"
    }

    @GetMapping("/rss")
    fun rss(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date? = null,
    ): StoryRssView =
        StoryRssView(
            baseUrl = baseUrl,
            endDate = endDate,
            startDate = startDate,
            storyService = storyService,
        )

    private fun findSubscriptions(user: UserModel): List<SubscriptionModel> =
        try {
            subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = user.id,
                    limit = 100,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve subscriptions", ex)
            emptyList()
        }

    private fun findStories(subscriptions: List<SubscriptionModel>, offset: Int): List<StoryModel> =
        try {
            storyService.search(
                SearchStoryRequest(
                    userIds = subscriptions.map { it.userId },
                    sortBy = StorySortStrategy.PUBLISHED,
                    sortOrder = SortOrder.DESCENDING,
                    limit = LIMIT,
                    offset = offset,
                    bubbleDownViewedStories = true,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve stories", ex)
            emptyList()
        }

    private fun recommend(): List<StoryModel> =
        try {
            storyService.recommend()
        } catch (ex: Exception) {
            LOGGER.warn("Unable to recommend stories", ex)
            emptyList()
        }

    private fun findWriters(user: UserModel?, subscriptions: List<SubscriptionModel>): List<UserModel> =
        try {
            val excludeIds = mutableListOf<Long>()
            excludeIds.addAll(subscriptions.map { it.userId })
            if (user != null) {
                excludeIds.add(user.id)
            }
            userService.search(
                SearchUserRequest(
                    excludeUserIds = excludeIds.toList(),
                    blog = true,
                    withPublishedStories = true,
                    active = true,
                    limit = 5,
                    sortBy = UserSortStrategy.POPULARITY,
                    sortOrder = SortOrder.DESCENDING,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve writers", ex)
            emptyList()
        }
}
