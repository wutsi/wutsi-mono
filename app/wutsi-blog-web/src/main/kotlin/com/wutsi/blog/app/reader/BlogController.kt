package com.wutsi.blog.app.reader

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.reader.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.reader.view.StoryRssView
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Controller
class BlogController(
    private val userService: UserService,
    private val storyService: StoryService,
    private val schemas: PersonSchemasGenerator,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val MAIN_PAGE_SIZE: Int = 20
    }

    override fun pageName() = PageName.BLOG

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)
        val stories = loadStories(blog, model, 0)

        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog, stories))
        if (stories.isEmpty() && blog.blog && blog.id == requestContext.currentUser()?.id) {
            model.addAttribute("showCreateStoryButton", true)
        }

        return "reader/blog"
    }

    @GetMapping("/@/{name}/stories")
    fun stories(@PathVariable name: String, @RequestParam offset: Int, model: Model): String {
        val blog = userService.get(name)
        model.addAttribute("blog", blog)
        loadStories(blog, model, offset)
        return "reader/fragment/stories"
    }

    @GetMapping("/@/{name}/subscribe")
    fun subscribe(
        @PathVariable name: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        userService.subscribeTo(blog.id)
        return if (returnUrl == null) {
            "redirect:/"
        } else {
            "redirect:$returnUrl"
        }
    }

    @GetMapping("/@/{name}/unsubscribe")
    fun unsubscribe(
        @PathVariable name: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        userService.unsubscribeFrom(blog.id)
        return if (returnUrl == null) {
            "redirect:/"
        } else {
            "redirect:$returnUrl"
        }
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

    private fun loadStories(blog: UserModel, model: Model, offset: Int = 0): List<StoryModel> {
        val limit = MAIN_PAGE_SIZE
        var stories = mutableListOf<StoryModel>()

        // Stories
        val pinnedStoryId = storyService.getPinnedStoryId(blog.id)
        stories.addAll(
            storyService.search(
                request = SearchStoryRequest(
                    userIds = listOf(blog.id),
                    status = StoryStatus.published,
                    sortBy = StorySortStrategy.published,
                    limit = limit,
                    offset = offset,
                    sortOrder = SortOrder.descending,
                ),
                pinnedStoryId = pinnedStoryId,
            ),
        )

        // Pin
        if (pinnedStoryId != null) {
            if (offset == 0) {
                stories = pin(stories, pinnedStoryId)
            } else {
                stories = stories.filter { it.id != pinnedStoryId }.toMutableList()
            }
        }

        if (stories.size >= limit) {
            val nextOffset = offset + limit
            model.addAttribute("moreUrl", "/@/${blog.name}/stories?offset=$nextOffset")
            model.addAttribute("nextOffset", nextOffset)
            model.addAttribute("offset", offset)
        }
        model.addAttribute("stories", stories)

        return stories
    }

    private fun pin(stories: MutableList<StoryModel>, pinnedStoryId: Long?): MutableList<StoryModel> {
        pinnedStoryId ?: return stories

        val result = mutableListOf<StoryModel>()
        val story = stories.find { it.id == pinnedStoryId }
        if (story != null) {
            result.add(story)
            result.addAll(stories.filter { it.id != story.id })
        } else {
            result.addAll(
                storyService.search(
                    request = SearchStoryRequest(
                        storyIds = listOf(pinnedStoryId),
                        status = StoryStatus.published,
                    ),
                ),
            )
            result.addAll(stories)
        }
        return result
    }

    private fun getPage(user: UserModel, stories: List<StoryModel>) = createPage(
        name = pageName(),
        title = user.fullName,
        description = user.biography ?: "",
        type = "profile",
        url = url(user),
        imageUrl = user.pictureUrl,
        schemas = schemas.generate(user),
        rssUrl = "${user.slug}/rss",
        preloadImageUrls = stories.map { it.thumbnailLargeUrl }.filter { !it.isNullOrBlank() }.take(1) as List<String>,
    )
}
