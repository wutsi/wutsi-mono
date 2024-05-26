package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.reader.view.StoryRssView
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
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
    private val cache: Cache,
    private val logger: KVLogger,
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
        loadStories(0, user.id, model)
        loadProducts(model)
        return "reader/home_authenticated"
    }

    @GetMapping("/home/stories")
    fun following(@RequestParam offset: Int, model: Model): String {
        val user = requestContext.currentUser()
            ?: return "reader/fragment/home-stories"

        // Subscriptions
        loadStories(offset, user.id, model)
        return "reader/fragment/home-stories"
    }

    private fun loadProducts(model: Model) {
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
                    userId = requestContext.currentUser()?.id,
                )
            )
        ).take(5)
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        logger.add("product_count", products.size)
    }

    private fun loadStories(
        offset: Int,
        userId: Long,
        model: Model,
    ): List<StoryModel> {
        val stories = findStories(offset, userId)

        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories)
            if (stories.size >= LIMIT) {
                model.addAttribute("moreUrl", "/home/stories?offset=" + (LIMIT + offset))
            }
        }
        return stories
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

    private fun findStories(offset: Int, userId: Long): List<StoryModel> =
        try {
            val stories = mutableListOf<StoryModel>()

            val subscriptions = findSubscriptions(userId)
            logger.add("subscription_count", subscriptions.size)

            if (subscriptions.isNotEmpty()) {
                stories.addAll(
                    storyService.search(
                        SearchStoryRequest(
                            sortBy = StorySortStrategy.RECOMMENDED,
                            limit = LIMIT,
                            offset = offset,
                            bubbleDownViewedStories = true,
                            dedupUser = true,
                            userIds = subscriptions.map { subscription -> subscription.userId },
                            searchContext = SearchStoryContext(
                                userId = userId
                            )
                        )
                    )
                )
                logger.add("story_count_subscribed", stories.size)
            }

            if (stories.size < LIMIT) {
                val excludeUserIds = mutableListOf<Long>()
                excludeUserIds.add(userId)
                excludeUserIds.addAll(stories.map { story -> story.user.id })

                val supplement = storyService.search(
                    SearchStoryRequest(
                        sortBy = StorySortStrategy.RECOMMENDED,
                        limit = LIMIT,
                        offset = offset,
                        bubbleDownViewedStories = true,
                        dedupUser = true,
                        wpp = true,
                        excludeUserIds = excludeUserIds,
                        searchContext = SearchStoryContext(
                            userId = userId
                        ),
                    )
                )
                logger.add("story_count_supplement", supplement.size)
                stories.addAll(supplement)
            }

            stories.take(LIMIT)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve stories", ex)
            emptyList()
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
