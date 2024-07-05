package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.reader.view.StoryRssView
import com.wutsi.blog.app.service.CategoryService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
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
    private val productService: ProductService,
    private val subscriptionService: SubscriptionService,
    private val categoryService: CategoryService,
    private val cache: Cache,
    private val logger: KVLogger,

    @Value("\${wutsi.application.preferred-countries}") private val preferredCountries: String,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeController::class.java)
        private const val LIMIT = 20
        private const val CACHE_KEY_WRITERS = "home.writers"
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
        val writers = findWriters()
        model.addAttribute("writers", writers)
        return "reader/home"
    }

    private fun recommended(model: Model): String {
        val user = requestContext.currentUser() ?: return "reader/home_authenticated"
        model.addAttribute("wallet", getWallet(user))

        val stories = loadSubscribedStories(user, model)
        loadPreferredStories(user, stories, model)
        loadProducts(user, model)
        return "reader/home_authenticated"
    }

    @GetMapping("/home/stories")
    fun stories(@RequestParam(name = "category-id") categoryId: Long, model: Model): String {
        val stories = storyService.search(
            SearchStoryRequest(
                sortBy = StorySortStrategy.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                limit = LIMIT,
                bubbleDownViewedStories = true,
                dedupUser = true,
                excludeStoriesFromSubscriptions = true,
                categoryIds = listOf(categoryId),
                userCountries = preferredCountries.split(","),
                searchContext = SearchStoryContext(
                    userId = requestContext.currentUser()?.id,
                ),
                activeUserOnly = true,
                status = StoryStatus.PUBLISHED,
            )
        )
        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories.take(5))

            val category = categoryService.search(SearchCategoryRequest(categoryIds = listOf(categoryId))).firstOrNull()
            model.addAttribute("category", category)
        }

        return "reader/fragment/home-stories"
    }

    private fun loadProducts(user: UserModel?, model: Model) {
        val products = productService.search(
            SearchProductRequest(
                limit = 20,
                status = ProductStatus.PUBLISHED,
                sortBy = ProductSortStrategy.RECOMMENDED,
                sortOrder = SortOrder.DESCENDING,
                available = true,
                bubbleDownPurchasedProduct = true,
                dedupUser = true,
                searchContext = SearchProductContext(
                    userId = user?.id,
                ),
            )
        ).take(5)
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        logger.add("product_count", products.size)
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

    private fun findSubscriptions(userId: Long): List<SubscriptionModel> =
        try {
            subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = userId,
                    limit = 100,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve subscriptions", ex)
            emptyList()
        }

    private fun loadSubscribedStories(user: UserModel, model: Model): List<StoryModel> {
        val subscriptions = findSubscriptions(user.id)
        logger.add("subscription_count", subscriptions.size)
        if (subscriptions.isNotEmpty()) {
            val stories = storyService.search(
                SearchStoryRequest(
                    sortBy = StorySortStrategy.RECOMMENDED,
                    limit = LIMIT,
                    bubbleDownViewedStories = true,
                    dedupUser = true,
                    userIds = subscriptions.map { subscription -> subscription.userId },
                    searchContext = SearchStoryContext(
                        userId = user.id,
                    ),
                    activeUserOnly = true,
                    status = StoryStatus.PUBLISHED,
                )
            )
            if (stories.isNotEmpty()) {
                model.addAttribute("stories", stories)
            }
            return stories
        }
        return emptyList()
    }

    private fun loadPreferredStories(user: UserModel, stories: List<StoryModel>, model: Model) {
        // Categories
        val preferredCategoryIds = if (user.preferredCategoryIds.isNotEmpty()) {
            categoryService.search(
                SearchCategoryRequest(
                    categoryIds = user.preferredCategoryIds,
                    limit = user.preferredCategoryIds.size
                )
            ).map { it.parentId }.toSet()
        } else {
            emptyList()
        }

        val categories = categoryService.search(SearchCategoryRequest(level = 0))
            .sortedByDescending { it.storyCount }
            .filter { !preferredCategoryIds.contains(it.id) }
        if (categories.isNotEmpty()) {
            model.addAttribute("categories", categories)
        }

        // Load stories
        if (user.preferredCategoryIds.isEmpty()) {
            return
        }
        val preferredStories = storyService.search(
            SearchStoryRequest(
                sortBy = StorySortStrategy.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                limit = LIMIT,
                bubbleDownViewedStories = true,
                dedupUser = true,
                excludeUserIds = stories.map { it.user.id }.toSet().toList(),
                categoryIds = user.preferredCategoryIds,
                userCountries = preferredCountries.split(","),
                activeUserOnly = true,
                status = StoryStatus.PUBLISHED,
            )
        )
        if (preferredStories.isNotEmpty()) {
            model.addAttribute("preferredStories", preferredStories)
        }
    }

    private fun findWriters(): List<UserModel> =
        try {
            cache.get(CACHE_KEY_WRITERS, Array<UserModel>::class.java)
                ?.toList()
                ?: findWritersFromServer()
        } catch (ex: Exception) {
            findWritersFromServer()
        }

    private fun findWritersFromServer(): List<UserModel> =
        try {
            val writers = userService.trending(5)
            cache.put(CACHE_KEY_WRITERS, writers.toTypedArray())
            writers
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve writers from server", ex)
            emptyList()
        }
}
