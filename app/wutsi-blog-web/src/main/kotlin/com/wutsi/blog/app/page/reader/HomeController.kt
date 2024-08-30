package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.model.ProductModel
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
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.apache.commons.lang3.time.DateUtils
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
    private val transactionService: TransactionService,
    private val cache: Cache,
    private val logger: KVLogger,

    @Value("\${wutsi.application.preferred-countries}") private val preferredCountries: String,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeController::class.java)
        private const val LIMIT = 20
        private const val CACHE_KEY_WRITERS = "home.writers"
        private const val CACHE_KEY_PRODUCTS = "home.products"
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
        model.addAttribute("writers", findWriters())
        model.addAttribute("products", findProducts())
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
        ).take(4)
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

    private fun findWriters(): List<UserModel> {
        val key = CACHE_KEY_WRITERS
        try {
            val writers = cache.get(key, Array<UserModel>::class.java)
                ?.toList()
                ?: findWritersFromServer()

            cache.put(key, writers.toTypedArray())
            return writers
        } catch (ex: Exception) {
            LOGGER.warn("Unable to fetch writers", ex)
            return emptyList()
        }
    }

    private fun findWritersFromServer(): List<UserModel> {
        return userService.trending(5)
    }

    private fun findProducts(): List<ProductModel> {
        val key = CACHE_KEY_PRODUCTS
        try {
            val products = cache.get(key, Array<ProductModel>::class.java)
                ?.toList()
                ?: findProductsFromServer()

            cache.put(key, products.toTypedArray())
            return products
        } catch (ex: Exception) {
            LOGGER.warn("Unable to fetch products", ex)
            return emptyList()
        }
    }

    private fun findProductsFromServer(): List<ProductModel> {
        // Fetch from transaction
        val today = Date()
        val txs = transactionService.search(
            SearchTransactionRequest(
                types = listOf(TransactionType.CHARGE),
                statuses = listOf(Status.SUCCESSFUL),
                creationDateTimeFrom = DateUtils.addDays(today, -8),
                creationDateTimeTo = today,
                limit = 20,
            )
        )
        val types = listOf(ProductType.EBOOK, ProductType.COMICS)
        val productMap = txs.groupBy { tx -> tx.product?.id }
        var products = txs.groupBy { tx -> tx.product }
            .mapNotNull { it.key }
            .filter { product ->
                types.contains(product.type) &&
                    product.available &&
                    product.status == ProductStatus.PUBLISHED
            }
            .sortedWith(
                object : Comparator<ProductModel> {
                    override fun compare(o1: ProductModel, o2: ProductModel): Int {
                        val sales1 = productMap[o1.id]?.size ?: 0
                        val sales2 = productMap[o2.id]?.size ?: 0
                        return sales2 - sales1
                    }
                }
            ).toMutableList()

        // Search for products
        products.addAll(
            productService.search(
                SearchProductRequest(
                    available = true,
                    status = ProductStatus.PUBLISHED,
                    types = types,
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                    limit = 20,
                    excludeProductIds = products.map { product -> product.id }
                )
            )
        )

        return products
            .filter { product -> !product.price.free }
            .distinctBy { product -> product.storeId }
            .take(8)
    }
}
