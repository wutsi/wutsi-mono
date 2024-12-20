package com.wutsi.blog.app.page.admin

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.form.PublishForm
import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractStoryController
import com.wutsi.blog.app.service.CategoryService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.WPPConfig
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.Date

@Controller
class TagController(
    private val categoryService: CategoryService,
    private val productService: ProductService,

    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR_TAG

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/tag")
    fun index(
        @PathVariable id: Long,
        @RequestParam(required = false) error: String? = null,
        model: Model,
    ): String {
        val categories = loadCategories(model)
        var story = getStory(id)
        if (story.category.id == 0L) {
            val categoryId = recommendCategory(story)
            if (categoryId != null) {
                val defaultCategory = categories.find { category -> category.id == categoryId }
                story = story.copy(
                    category = defaultCategory ?: CategoryModel(),
                )
            }
        }

        val products = loadProducts(story.user, model)

        model.addAttribute("story", story)
        model.addAttribute("error", error)
        loadScheduledPublishDate(story, model)

        val readability = service.readability(id)
        model.addAttribute("readability", readability)

        val wpp = service.validateWPPEligibility(id)
        model.addAttribute("wpp", wpp)
        model.addAttribute("WPPConfig", WPPConfig::class.java)

        return "admin/tag"
    }

    /**
     * Recommend category by fetching the last 20 publications, and select the most popular cateogories
     */
    private fun recommendCategory(story: StoryModel): Long? {
        val stories = service.search(
            SearchStoryRequest(
                userIds = listOf(story.user.id),
                status = StoryStatus.PUBLISHED,
                sortBy = StorySortStrategy.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                limit = 20,
            )
        )
        val storiesByCategoryIds = stories.groupBy { it.category.id }
        val entries = storiesByCategoryIds.entries
            .filter { entry -> entry.key != 0L }
            .sortedByDescending { entry -> entry.value.size }
        return entries.firstOrNull()?.key
    }

    private fun loadCategories(model: Model): List<CategoryModel> {
        val categories = categoryService.all()
        model.addAttribute("categories", categories)
        return categories
    }

    private fun loadProducts(user: UserModel, model: Model): List<ProductModel> {
        if (user.storeId == null) {
            return emptyList()
        }

        val products = productService.search(
            SearchProductRequest(
                storeIds = listOf(user.storeId),
                status = ProductStatus.PUBLISHED,
                available = true,
                sortBy = ProductSortStrategy.TITLE,
                limit = 200,
            )
        )
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        return products
    }

    private fun loadScheduledPublishDate(story: StoryModel, model: Model) {
        if (story.published) {
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val tomorrow = DateUtils.addDays(Date(), 1)
        model.addAttribute("minScheduledPublishDate", dateFormat.format(tomorrow))
        model.addAttribute(
            "scheduledPublishDate",
            story.scheduledPublishDateTimeAsDate?.let { dateFormat.format(it) } ?: "",
        )
        model.addAttribute("publishNow", story.scheduledPublishDateTimeAsDate == null)
    }

    @GetMapping("/me/story/tag/submit")
    fun submit(@ModelAttribute editor: PublishForm): String {
        try {
            service.publish(editor)
            return if (editor.publishNow) {
                "redirect:/me/story/${editor.id}/share"
            } else {
                "redirect:/me/draft"
            }
        } catch (ex: Exception) {
            return "redirect:/me/story/${editor.id}/tag?error=publish_error"
        }
    }
}
