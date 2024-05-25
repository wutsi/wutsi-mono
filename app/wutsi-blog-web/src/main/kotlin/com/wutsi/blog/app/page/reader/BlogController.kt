package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.reader.view.StoryRssView
import com.wutsi.blog.app.service.ImageType
import com.wutsi.blog.app.service.OpenGraphImageGenerator
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieHelper.preSubscribeKey
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Date

@Controller
class BlogController(
    private val userService: UserService,
    private val storyService: StoryService,
    private val subscriptionService: SubscriptionService,
    private val productService: ProductService,
    private val schemas: PersonSchemasGenerator,
    private val imageService: ImageService,
    private val logger: KVLogger,
    private val request: HttpServletRequest,
    private val opengraph: OpenGraphImageGenerator,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT: Int = 20
        private val LOGGER = LoggerFactory.getLogger(BlogController::class.java)
    }

    override fun pageName(): String =
        if (request.servletPath.lowercase().endsWith("/about")) {
            PageName.BLOG_ABOUT
        } else {
            PageName.BLOG
        }

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        try {
            // Blog
            val blog = userService.get(name)
            logger.add("blog_id", blog.id)
            model.addAttribute("blog", blog)

            // Should subscribe?
            if (shouldPreSubscribe(blog)) {
                model.addAttribute("page", getPage(blog, emptyList()))
                preSubscribed(blog)

                logger.add("show_pre_subscribe", true)
                return "reader/blog_pre_subscribe"
            } else {
                logger.add("show_pre_subscribe", false)
            }

            // Whallet
            logger.add("wallet_id", blog.walletId)
            val wallet = getWallet(blog)
            model.addAttribute("wallet", wallet)

            // Stories
            val user = requestContext.currentUser()
            val stories = loadStories(blog, user, model, 0)
            logger.add("user_id", user?.id)
            logger.add("story_count", stories.size)

            model.addAttribute("page", getPage(blog, stories))
            if (stories.isEmpty() && blog.blog && blog.id == requestContext.currentUser()?.id) {
                model.addAttribute("showCreateStoryButton", true)
            }

            // Products
            logger.add("store_id", blog.storeId)
            val store = getStore(blog)
            if (store != null) {
                val products = loadProducts(store, model)
                logger.add("product_count", products.size)
            }

            return "reader/blog"
        } catch (ex: HttpClientErrorException.NotFound) {
            logger.add("not_found", true)
            logger.add("not_found_error", ex.message)
            return notFound(model)
        }
    }

    private fun loadProducts(store: StoreModel, model: Model): List<ProductModel> {
        try {
            val products = productService.search(
                SearchProductRequest(
                    storeIds = listOf(store.id),
                    available = true,
                    limit = 20,
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                    status = ProductStatus.PUBLISHED,
                    currentUserId = requestContext.currentUser()?.id,
                )
            ).shuffled().take(3)
            if (products.isNotEmpty()) {
                model.addAttribute("products", products)
            }
            return products
        } catch (ex: Exception) {
            LOGGER.warn("Unable to load products", ex)
            return emptyList()
        }
    }

    @GetMapping("/me/about")
    fun about(): String {
        val user = requestContext.currentUser()!!
        return "redirect:/@/${user.name}/about"
    }

    @GetMapping("/@/{name}/about")
    fun about(@PathVariable name: String, model: Model): String {
        try {
            val blog = userService.get(name)
            logger.add("blog_id", blog.id)

            model.addAttribute("blog", blog)
            model.addAttribute("page", getPage(blog, emptyList()))

            return "reader/about"
        } catch (ex: HttpClientErrorException.NotFound) {
            logger.add("not_found", true)
            logger.add("not_found_error", ex.message)
            return notFound(model)
        }
    }

    @GetMapping("/@/{name}/image.png")
    fun image(@PathVariable name: String): ResponseEntity<InputStreamResource> {
        val blog = userService.get(name)
        if (!blog.blog) {
            return ResponseEntity.notFound().build()
        }

        val out = ByteArrayOutputStream()
        opengraph.generate(
            type = ImageType.PROFILE,
            pictureUrl = blog.pictureUrl?.let { pictureUrl ->
                imageService.transform(
                    url = pictureUrl,
                    transformation = Transformation(
                        Dimension(
                            OpenGraphImageGenerator.IMAGE_WIDTH,
                            OpenGraphImageGenerator.IMAGE_HEIGHT,
                        ),
                    ),
                )
            },
            title = blog.fullName,
            description = blog.biography,
            language = blog.language,
            output = out,
        )

        val input = ByteArrayInputStream(out.toByteArray())
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(InputStreamResource(input))
    }

    private fun notFound(model: Model): String {
        val subscriptions = requestContext.currentUser()?.let {
            subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = it.id,
                    limit = 100,
                ),
            )
        } ?: emptyList()

        val blogs = userService.search(
            SearchUserRequest(
                excludeUserIds = subscriptions.map { it.userId },
                blog = true,
                active = true,
                limit = 5,
                sortBy = UserSortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                minSubscriberCount = WPPConfig.MIN_SUBSCRIBER_COUNT,
                minPublishStoryCount = WPPConfig.MIN_STORY_COUNT,
                minCreationDateTime = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS)
            ),
        )
        if (blogs.isNotEmpty()) {
            model.addAttribute("blogs", blogs)
        }

        model.addAttribute(
            "page",
            createPage(
                name = PageName.BLOG_NOT_FOUND,
                title = requestContext.getMessage("page.home.metadata.title"),
                description = requestContext.getMessage("page.home.metadata.description"),
                robots = "noindex,nofollow",
            ),
        )

        return "reader/blog_not_found"
    }

    @GetMapping("/@/{name}/stories")
    fun stories(@PathVariable name: String, @RequestParam offset: Int, model: Model): String {
        val blog = userService.get(name)
        val user = requestContext.currentUser()
        model.addAttribute("blog", blog)
        loadStories(blog, user, model, offset)
        return "reader/fragment/stories"
    }

    @GetMapping("/@/{name}/rss")
    fun rss(
        @PathVariable name: String,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date? = null,
    ): StoryRssView =
        StoryRssView(
            user = userService.get(name),
            baseUrl = baseUrl,
            endDate = endDate,
            startDate = startDate,
            storyService = storyService,
        )

    private fun loadStories(
        blog: UserModel,
        user: UserModel?,
        model: Model,
        offset: Int = 0,
    ): List<StoryModel> {
        val limit = LIMIT
        var stories = storyService.search(
            request = SearchStoryRequest(
                userIds = listOf(blog.id),
                status = StoryStatus.PUBLISHED,
                sortBy = StorySortStrategy.PUBLISHED,
                limit = limit,
                offset = offset,
                sortOrder = SortOrder.DESCENDING,
                bubbleDownViewedStories = (blog.id != user?.id),
            ),
            pinStoryId = blog.pinStoryId,
        ).toMutableList()

        // Pin
        if (blog.pinStoryId != null) {
            if (offset == 0) {
                stories = pin(stories, blog.pinStoryId)
            } else {
                stories = stories.filter { it.id != blog.pinStoryId }.toMutableList()
            }
        }

        if (stories.isNotEmpty()) {
            if (stories.size >= limit) {
                val nextOffset = offset + limit
                model.addAttribute("moreUrl", "/@/${blog.name}/stories?offset=$nextOffset")
                model.addAttribute("nextOffset", nextOffset)
                model.addAttribute("offset", offset)
            }
            model.addAttribute("stories", stories)
        }

        return stories
    }

    private fun pin(stories: MutableList<StoryModel>, pinStoryId: Long?): MutableList<StoryModel> {
        pinStoryId ?: return stories

        val result = mutableListOf<StoryModel>()
        val story = stories.find { it.id == pinStoryId }
        if (story != null) {
            result.add(story)
            result.addAll(stories.filter { it.id != story.id })
        } else {
            getPinStory(pinStoryId)?.let { result.add(it) }
            result.addAll(stories)
        }
        return result
    }

    private fun getPinStory(storyId: Long): StoryModel? =
        try {
            storyService.search(
                request = SearchStoryRequest(
                    storyIds = listOf(storyId),
                    status = StoryStatus.PUBLISHED,
                ),
            ).firstOrNull()
        } catch (ex: Exception) {
            null
        }

    private fun getPage(user: UserModel, stories: List<StoryModel>) = createPage(
        name = pageName(),
        title = user.fullName,
        description = user.biography ?: "",
        type = "profile",
        url = url(user),
        imageUrl = if (user.blog) {
            "$baseUrl/@/${user.name}/image.png"
        } else {
            null
        },
        schemas = schemas.generate(user),
        rssUrl = "${user.slug}/rss",
        preloadImageUrls = stories.map { it.thumbnailLargeUrl }.filter { !it.isNullOrBlank() }.take(1) as List<String>,
    )

    private fun shouldPreSubscribe(blog: UserModel): Boolean =
        !requestContext.isBot() && // User not a bot
                !blog.subscribed && // User not subscribed
                blog.id != requestContext.currentUser()?.id && // User is not author
                blog.publishStoryCount > 0 && // User has stories published
                CookieHelper.get(
                    preSubscribeKey(blog),
                    requestContext.request,
                ).isNullOrEmpty() // Control frequency

    private fun preSubscribed(blog: UserModel) {
        val key = preSubscribeKey(blog)
        CookieHelper.put(key, "1", requestContext.request, requestContext.response, CookieHelper.ONE_DAY_SECONDS)
    }
}
