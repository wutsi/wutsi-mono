package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.backend.MailBackend
import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.backend.ShareBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.form.PublishForm
import com.wutsi.blog.app.mapper.StoryMapper
import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.ReadabilityModel
import com.wutsi.blog.app.model.StoryForm
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.model.WPPValidationModel
import com.wutsi.blog.app.service.ejs.EJSFilterSet
import com.wutsi.blog.app.service.ejs.EJSInterceptorSet
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.UnpublishStoryCommand
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.tracing.TracingContext
import org.apache.commons.lang3.time.DateUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

@Service
class StoryService(
    private val requestContext: RequestContext,
    private val mapper: StoryMapper,
    private val ejsJsonReader: EJSJsonReader,
    private val ejsHtmlWriter: EJSHtmlWriter,
    private val ejsFilters: EJSFilterSet,
    private val ejsIntercetors: EJSInterceptorSet,
    private val userService: UserService,
    private val storyBackend: StoryBackend,
    private val likeBackend: LikeBackend,
    private val pinBackend: PinBackend,
    private val shareBackend: ShareBackend,
    private val mailBackend: MailBackend,
    private val tracingContext: TracingContext,
    private val kpiService: KpiService,
    private val categoryService: CategoryService,
    private val productService: ProductService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryService::class.java)
    }

    fun save(editor: StoryForm): StoryForm {
        val storyId = if (shouldCreate(editor)) {
            storyBackend.create(
                CreateStoryCommand(
                    userId = requestContext.currentUser()?.id,
                    title = editor.title,
                    content = editor.content,
                ),
            ).storyId
        } else {
            storyBackend.update(
                UpdateStoryCommand(
                    storyId = editor.id!!,
                    title = editor.title,
                    content = editor.content,
                ),
            )
            editor.id
        }

        return StoryForm(
            id = storyId,
            title = editor.title,
            content = editor.content,
        )
    }

    fun get(id: Long): StoryModel {
        val story = storyBackend.get(id).story
        val user = userService.get(story.userId)
        val product = story.productId?.let { productId ->
            try {
                productService.get(productId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to load Product#$productId", ex)
                null
            }
        }
        return mapper.toStoryModel(story, user, product)
    }

    fun search(request: SearchStoryRequest, pinStoryId: Long? = null): List<StoryModel> {
        val stories = storyBackend.search(request).stories
        if (stories.isEmpty()) {
            return emptyList()
        }

        val users = searchUserMap(stories)
        val categories = searchCategoryMap(stories)
        val products = searchProductMap(stories)
        return stories.map { story ->
            mapper.toStoryModel(
                story,
                users[story.userId],
                pinStoryId,
                story.categoryId?.let { categoryId -> categories[categoryId] },
                story.productId?.let { productId -> products[productId] }
            )
        }
    }

    private fun searchProductMap(stories: List<StorySummary>): Map<Long, ProductModel> {
        val productIds = stories.mapNotNull { story -> story.productId }.toSet().toList()
        if (productIds.isEmpty()) {
            return emptyMap()
        }
        return productService.search(
            SearchProductRequest(
                productIds = productIds,
                available = true,
                status = ProductStatus.PUBLISHED,
                limit = productIds.size,
            )
        ).associateBy { it.id }
    }

    private fun searchCategoryMap(stories: List<StorySummary>): Map<Long, CategoryModel> {
        val categoryIds = stories.mapNotNull { story -> story.categoryId }.toSet().toList()
        if (categoryIds.isEmpty()) {
            return emptyMap()
        }

        return categoryService.search(
            SearchCategoryRequest(
                categoryIds = categoryIds,
                limit = categoryIds.size
            )
        ).associateBy { it.id }
    }

    fun recommend(
        blogId: Long? = null,
        excludeStoryIds: List<Long> = emptyList(),
        excludeUserIds: List<Long> = emptyList(),
        limit: Int = 20,
        debupUser: Boolean = false,
        minStoriesPerBlog: Int? = null,
        minBlogAgeMonths: Int? = null,
    ): List<StoryModel> =
        recommend(
            blogId?.let { listOf(it) } ?: emptyList(),
            excludeStoryIds,
            excludeUserIds,
            limit,
            debupUser,
            minStoriesPerBlog,
            minBlogAgeMonths,
        )

    fun trending(limit: Int): List<StoryModel> {
        val kpis = kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(KpiType.DURATION),
                dimension = Dimension.ALL,
                fromDate = LocalDate.now().minusDays(30),
            )
        ).sortedByDescending { it.value }
        val storyIds = kpis
            .map { it.targetId }
            .toSet()
            .toList()
            .take(limit)

        return search(
            SearchStoryRequest(
                storyIds = storyIds,
                limit = storyIds.size,
                status = StoryStatus.PUBLISHED,
                sortBy = StorySortStrategy.NONE,
                dedupUser = true,
                bubbleDownViewedStories = true,
            )
        )
    }

    fun recommend(
        blogIds: List<Long>,
        excludeStoryIds: List<Long>,
        excludeUserIds: List<Long>,
        limit: Int,
        dedupBlog: Boolean = false,
        minStoriesPerBlog: Int? = null,
        minBlogAgeMonths: Int? = null,
    ): List<StoryModel> {
        val storyIds = storyBackend.recommend(
            RecommendStoryRequest(
                readerId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                limit = 200,
            ),
        ).storyIds.filter { !excludeStoryIds.contains(it) }
        if (storyIds.isEmpty()) {
            return emptyList()
        }

        val minCreateDateTime = minBlogAgeMonths?.let {
            DateUtils.addMonths(Date(), -minBlogAgeMonths)
        }
        return search(
            SearchStoryRequest(
                userIds = blogIds,
                storyIds = storyIds,
                status = StoryStatus.PUBLISHED,
                limit = storyIds.size,
                sortBy = StorySortStrategy.NONE,
                bubbleDownViewedStories = true,
                dedupUser = dedupBlog,
            ),
        ).filter {
            !it.thumbnailUrl.isNullOrEmpty() &&
                !excludeUserIds.contains(it.user.id) &&
                (minStoriesPerBlog != null && it.user.publishStoryCount > minStoriesPerBlog) &&
                (minCreateDateTime != null && it.user.creationDateTime.before(minCreateDateTime))
        }.take(limit)
    }

    fun generateHtmlContent(story: StoryModel, summary: Boolean = false): String {
        if (story.content == null) {
            return ""
        }

        // EJS
        val ejs = ejsJsonReader.read(story.content, summary)
        ejsIntercetors.filter(ejs, story)

        // HTML
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)

        val doc = Jsoup.parse(html.toString())
        ejsFilters.filter(story, doc)
        return doc.html()
    }

    fun publish(form: PublishForm) {
        storyBackend.publish(
            PublishStoryCommand(
                storyId = form.id,
                title = form.title,
                tagline = form.tagline,
                categoryId = form.categoryId.ifEmpty { null }?.toLong(),
                productId = form.productId.ifEmpty { null }?.toLong(),
                access = form.access,
                scheduledPublishDateTime = if (form.publishNow) {
                    null
                } else {
                    SimpleDateFormat("yyyy-MM-dd").parse(form.scheduledPublishDate)
                },
            ),
        )
    }

    fun unpublish(storyId: Long) {
        storyBackend.unpublish(UnpublishStoryCommand(storyId))
    }

    fun import(url: String): Long =
        storyBackend.import(
            ImportStoryCommand(
                url = url,
                userId = requestContext.currentUser()?.id ?: -1,
            ),
        ).storyId

    fun readability(id: Long): ReadabilityModel =
        mapper.toReadabilityModel(
            storyBackend.readability(id).readability
        )

    fun validateWPPEligibility(id: Long): WPPValidationModel =
        mapper.toWPPValidationModel(
            storyBackend.validateWPPEligibility(id).validation
        )

    fun delete(id: Long) {
        storyBackend.delete(DeleteStoryCommand(id))
    }

    fun like(storyId: Long) {
        likeBackend.like(
            LikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun unlike(storyId: Long) {
        likeBackend.unlike(
            UnlikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun share(storyId: Long) {
        shareBackend.share(
            ShareStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
            ),
        )
    }

    fun pin(storyId: Long) {
        pinBackend.pin(
            PinStoryCommand(
                storyId = storyId,
            ),
        )
    }

    fun unpin(storyId: Long) {
        pinBackend.unpin(
            UnpinStoryCommand(storyId),
        )
    }

    fun sendDailyMail(storyId: Long) {
        mailBackend.sendDaily(
            SendStoryDailyEmailCommand(storyId),
        )
    }

    private fun shouldCreate(editor: StoryForm) = (editor.id == null || editor.id == 0L) && !isEmpty(editor)

    private fun isEmpty(editor: StoryForm): Boolean {
        if (editor.title.trim().isNotEmpty()) {
            return false
        }

        val doc = ejsJsonReader.read(editor.content)
        val html = StringWriter()
        ejsHtmlWriter.write(doc, html)
        return Jsoup.parse(html.toString()).body().text().trim().isEmpty()
    }

    private fun searchUserMap(stories: List<StorySummary>): Map<Long, UserModel?> {
        val userIds = stories.map { it.userId }.toSet().toList()
        return if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.search(
                SearchUserRequest(
                    userIds = userIds,
                    limit = userIds.size,
                    offset = 0,
                ),
            ).associateBy { it.id }
        }
    }
}
