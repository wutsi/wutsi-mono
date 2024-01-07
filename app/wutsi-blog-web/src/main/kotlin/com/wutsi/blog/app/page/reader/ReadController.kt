package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.form.TrackForm
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractStoryReadController
import com.wutsi.blog.app.page.reader.schemas.StorySchemasGenerator
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.payment.core.Status
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException
import java.util.Date

@Controller
class ReadController(
    private val schemas: StorySchemasGenerator,
    private val logger: KVLogger,
    private val trackingBackend: TrackingBackend,
    private val tracingContext: TracingContext,
    private val productService: ProductService,
    private val transactionService: TransactionService,

    ejsJsonReader: EJSJsonReader,
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryReadController(ejsJsonReader, service, requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReadController::class.java)
    }

    override fun pageName() = PageName.READ

    override fun requiredPermissions() = listOf(Permission.reader)

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun generateSchemas(story: StoryModel) = schemas.generate(story)

    @GetMapping("/read/{id}/{title}")
    fun read(
        @PathVariable id: String,
        @PathVariable title: String,
        @RequestParam(required = false) like: String? = null,
        @RequestParam(required = false, name = "like-key") likeKey: String? = null,
        @RequestParam(required = false, name = "utm_from") from: String? = null,
        model: Model,
    ): String {
        return read(id, model, like, likeKey, from)
    }

    @GetMapping("/read/{id}")
    fun read(
        @PathVariable id: String,
        model: Model,
        @RequestParam(required = false) like: String? = null,
        @RequestParam(required = false, name = "like-key") likeKey: String? = null,
        @RequestParam(required = false, name = "utm_from") from: String? = null,
    ): String {
        try {
            val storyId = id.toLong()
            val user = requestContext.currentUser()
            val story = getStory(storyId)
            val showPaywall = shouldShowPaywall(story, user)

            // Load the story
            loadPage(story, model, showPaywall)

            // Like
            if (like == "1" && like(storyId, likeKey)) {
                // OK
            }

            // Display subscribe modal
            if (shouldShowSubscribeModal(story.user, user)) {
                model.addAttribute("showSubscribeModal", true)
                subscribedModalDisplayed(story.user)

                logger.add("show_subscribe_modal", true)
            } else {
                logger.add("show_subscribe_modal", false)
            }

            // Display the paywall
            model.addAttribute("showPaywall", showPaywall)

            // Tagged product and recommendation
            if (!showPaywall) {
                loadProducts(story, model)
                loadRecommendations(story, model)
            }

            return "reader/read"
        } catch (ex: HttpClientErrorException) {
            if (ex.statusCode == HttpStatus.NOT_FOUND) {
                logger.add("not_found", true)
                logger.add("not_found_error", ex.message)
                return notFound(model)
            } else {
                throw ex
            }
        } catch (ex: ForbiddenException) {
            return notFound(model)
        }
    }

    private fun loadProducts(story: StoryModel, model: Model) {
        val store = getStore(story.user) ?: return
        val products = productService.search(
            SearchProductRequest(
                storeIds = listOf(store.id),
                available = true,
                sortBy = ProductSortStrategy.ORDER_COUNT,
                sortOrder = SortOrder.DESCENDING,
                limit = 20,
                storyId = story.id,
            )
        )
        if (products.isEmpty()) {
            return
        }
        val product = products[0]
        model.addAttribute("product", product)
        model.addAttribute("products", products.filter { it.id != product.id }.take(3))
    }

    private fun shouldShowPaywall(story: StoryModel, user: UserModel?): Boolean =
        if (story.user.id == user?.id) {
            false
        } else if (story.access == StoryAccess.SUBSCRIBER) {
            !story.user.subscribed && (story.user.id != user?.id)
        } else if (story.access == StoryAccess.DONOR && story.user.walletId != null) {
            !hasDonatedInPastYear(story.user, user)
        } else {
            false
        }

    private fun hasDonatedInPastYear(blog: UserModel, user: UserModel?): Boolean {
        if (user == null) {
            return false
        }
        return transactionService.search(
            SearchTransactionRequest(
                walletId = blog.walletId,
                userId = user.id,
                statuses = listOf(Status.SUCCESSFUL),
                types = listOf(TransactionType.DONATION),
                creationDateTimeFrom = DateUtils.addYears(Date(), -1),
                limit = 1,
            )
        ).isNotEmpty()
    }

    private fun notFound(model: Model): String {
        model.addAttribute(
            "page",
            createPage(
                name = PageName.STORY_NOT_FOUND,
                title = requestContext.getMessage("page.home.metadata.title"),
                description = requestContext.getMessage("page.home.metadata.description"),
                robots = "noindex,nofollow",
            ),
        )

        val stories = service.search(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                dedupUser = true,
                limit = 10,
            ),
        ).map { it.copy(slug = "${it.slug}?utm_from=not-found") }
        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories)
        }
        return "reader/story_not_found"
    }

    private fun like(id: Long, key: String?): Boolean {
        key ?: return false

        try {
            val parts = key.split("_") // First part is dummy GUID
            val storyId = parts[1].toLong()
            if (storyId == id) {
                service.like(storyId)
                return true
            } else {
                logger.add("invalid_like_key", true)
            }
        } catch (ex: Exception) { // Never fail on error
            LOGGER.warn("Unable to like to Story#$id with Key=$key", ex)
        }
        return false
    }

    @ResponseBody
    @PostMapping("/read/{id}/like")
    fun like(@PathVariable id: Long) {
        service.like(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/unlike")
    fun unlike(@PathVariable id: Long) {
        service.unlike(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/pin")
    fun pin(@PathVariable id: Long) {
        service.pin(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/unpin")
    fun unpin(@PathVariable id: Long) {
        service.unpin(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/share")
    fun share(@PathVariable id: Long) {
        service.share(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/track")
    fun track(@PathVariable id: Long, @RequestBody form: TrackForm): Map<String, String> {
        val user = requestContext.currentUser()
        if (user?.superUser == true || service.get(id).id == user?.id) {
            logger.add("track_ignored", true)
            return emptyMap()
        }

        // Track
        trackingBackend.push(
            PushTrackRequest(
                time = form.time,
                correlationId = form.hitId,
                productId = id.toString(),
                event = form.event,
                deviceId = tracingContext.deviceId(),
                url = form.url,
                ua = form.ua,
                value = form.value,
                page = PageName.READ,
                referrer = form.referrer,
                accountId = user?.id?.toString(),
                ip = requestContext.remoteIp(),
            ),
        )

        // Finished
        if (form.event == "readend") {
            val readTime = try {
                form.value!!.toLong()
            } catch (ex: Exception) {
                -1
            }
            service.view(id, readTime)
        }

        return emptyMap()
    }

    @ResponseBody
    @PostMapping("/read/{id}/send-daily")
    fun sendDaily(@PathVariable id: Long) {
        service.sendDailyMail(id)
    }

    private fun loadRecommendations(story: StoryModel, model: Model) {
        try {
            val stories = service.search(
                SearchStoryRequest(
                    userIds = listOf(story.user.id),
                    status = StoryStatus.PUBLISHED,
                    sortBy = StorySortStrategy.PUBLISHED,
                    sortOrder = SortOrder.DESCENDING,
                    bubbleDownViewedStories = true,
                    limit = 21,
                )
            ).filter { it.id != story.id }
                .take(10)
            model.addAttribute(
                "stories",
                stories.map { it.copy(slug = "${it.slug}?utm_from=read-also") },
            )
            model.addAttribute("layout", "summary")

            logger.add("recommended_stories", stories.size)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find Story recommendations", ex)
        }
    }

    private fun shouldShowSubscribeModal(blog: UserModel, user: UserModel?): Boolean =
        !blog.subscribed && // User not subscribed
            blog.id != user?.id && // User is not author
            CookieHelper.get(
                CookieHelper.preSubscribeKey(blog),
                requestContext.request,
            ).isNullOrEmpty() // Control frequency

    private fun subscribedModalDisplayed(blog: UserModel) {
        val key = CookieHelper.preSubscribeKey(blog)
        CookieHelper.put(key, "1", requestContext.request, requestContext.response, CookieHelper.ONE_DAY_SECONDS)
    }
}
