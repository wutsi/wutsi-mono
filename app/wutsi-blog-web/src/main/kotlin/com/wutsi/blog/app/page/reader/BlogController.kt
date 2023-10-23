package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.reader.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.reader.view.StoryRssView
import com.wutsi.blog.app.service.ImageType
import com.wutsi.blog.app.service.OpenGraphImageGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieHelper.preSubscribeKey
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import com.wutsi.blog.wpp.dto.WPPConfig
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
    private val schemas: PersonSchemasGenerator,
    private val imageService: ImageService,
    private val logger: KVLogger,
    private val request: HttpServletRequest,
    private val opengraph: OpenGraphImageGenerator,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(BlogController::class.java)

        const val LIMIT: Int = 20
        const val FROM = "blog"
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

            // Stories
            val user = requestContext.currentUser()
            val stories = loadStories(blog, user, model, 0)
            model.addAttribute("page", getPage(blog, stories))
            model.addAttribute("wallet", getWallet(blog))
            if (stories.isEmpty() && blog.blog && blog.id == requestContext.currentUser()?.id) {
                model.addAttribute("showCreateStoryButton", true)
            }

            // Recommendation
            val recommended = getRecommendedStories(blog, stories)
            if (recommended.isNotEmpty()) {
                model.addAttribute("recommendedStories", recommended)
            }

            // Announcement
            loadAnnouncements(blog, model)

            return "reader/blog"
        } catch (ex: HttpClientErrorException.NotFound) {
            logger.add("not_found", true)
            logger.add("not_found_error", ex.message)
            return notFound(model)
        }
    }

    private fun loadAnnouncements(blog: UserModel, model: Model) {
        if (!blog.blog) {
            return
        }

        val announcement = if (blog.subscriberCount == 0L) {
            "subscriber"
        } else if (getToggles().monetization && !blog.country.isNullOrEmpty() && blog.canEnableMonetization) {
            "monetization"
        } else {
            null
        }

        model.addAttribute("announcement", announcement)
    }

    @GetMapping("/@/{name}/about")
    fun about(@PathVariable name: String, model: Model): String {
        try {
            val blog = userService.get(name)

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

    @GetMapping("/@/{name}/subscribe")
    fun subscribe(
        @PathVariable name: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "story-id", required = false) storyId: Long? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        subscriptionService.subscribeTo(blog.id, storyId, storyId?.let { "story" } ?: "blog")
        return redirectTo(returnUrl, "subscribe")
    }

    @GetMapping("/@/{name}/unsubscribe")
    fun unsubscribe(
        @PathVariable name: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        subscriptionService.unsubscribeFrom(blog.id)
        return redirectTo(returnUrl, "unsubscribe")
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

        if (stories.size >= limit) {
            val nextOffset = offset + limit
            model.addAttribute("moreUrl", "/@/${blog.name}/stories?offset=$nextOffset")
            model.addAttribute("nextOffset", nextOffset)
            model.addAttribute("offset", offset)
        }
        model.addAttribute("stories", stories.map { it.copy(slug = "${it.slug}?utm_from=$FROM") })

        return stories
    }

    private fun getRecommendedStories(blog: UserModel, stories: List<StoryModel>): List<StoryModel> =
        try {
            storyService.recommend(
                blogId = blog.id,
                excludeStoryIds = stories.map { it.id },
                limit = 20,
            ).take(5)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to load recommended stories", ex)
            emptyList()
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
        !blog.subscribed && // User not subscribed
            blog.id != requestContext.currentUser()?.id && // User is not author
            CookieHelper.get(
                preSubscribeKey(blog),
                requestContext.request,
            ).isNullOrEmpty() // Control frequency

    private fun preSubscribed(blog: UserModel) {
        val key = preSubscribeKey(blog)
        CookieHelper.put(key, "1", requestContext.request, requestContext.response, CookieHelper.ONE_DAY_SECONDS)
    }
}
