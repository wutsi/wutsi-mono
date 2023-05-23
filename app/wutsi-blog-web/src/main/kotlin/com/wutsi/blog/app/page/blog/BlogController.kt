package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.blog.service.PinService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryMapper
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryContext
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BlogController(
    private val userService: UserService,
    private val followerService: FollowerService,
    private val storyService: StoryService,
    private val schemas: PersonSchemasGenerator,
    private val pinService: PinService,
    private val mapper: StoryMapper,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val MAIN_PAGE_SIZE: Int = 10
        const val SIDEBAR_SIZE: Int = 10
    }

    override fun pageName() = PageName.BLOG

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)

        model.addAttribute("blog", blog)

        return if (blog.blog) {
            loadWriter(blog, model)
        } else {
            val followingUserIds = followerService.searchFollowingUserIds()
                .filter { it != blog.id }

            loadReader(followingUserIds, blog, model)
        }
    }

    @GetMapping("/@/{name}/my-stories")
    fun myStories(@PathVariable name: String, @RequestParam offset: Int, model: Model): String {
        val blog = userService.get(name)
        val stories = loadMyStories(blog, null, model, offset)

        model.addAttribute("blog", blog)
        model.addAttribute("stories", stories)

        return "page/blog/stories"
    }

    @GetMapping("/@/{name}/writer-sidebar")
    fun writerSidebar(@PathVariable name: String, model: Model): String {
        // Blog
        val blog = userService.get(name)
        model.addAttribute("blog", blog)
        model.addAttribute("user", requestContext.currentUser())

        // Recent stories
        val stories = storyService.search(
            request = SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.recommended,
                sortOrder = SortOrder.descending,
                limit = 2 * SIDEBAR_SIZE,
                context = SearchStoryContext(
                    deviceId = requestContext.deviceId(),
                ),
            ),
        ).filter { it.user.id != blog.id }

        // Filter follower stories
        val followingUserIds = followerService.searchFollowingUserIds()
            .filter { it != blog.id }
        val followingStories = stories.filter { followingUserIds.contains(it.user.id) }
        model.addAttribute("followingStories", followingStories)

        // Recent stories
        val followingStoryIds = followingStories.map { it.id }
        val latestStories = stories.filter { !followingStoryIds.contains(it.id) }
        model.addAttribute("latestStories", latestStories)

        return "page/blog/writer_sidebar"
    }

    private fun loadWriter(blog: UserModel, model: Model): String {
        val pin = loadPin(blog, model)
        val stories = loadMyStories(blog, pin, model)

        shouldShowFollowButton(blog, model)
        shouldShowCreateStory(blog, stories, model)

        model.addAttribute("sidebarUrl", "/@/${blog.name}/writer-sidebar")
        model.addAttribute("page", getPage(blog, stories))

        return "page/blog/writer"
    }

    private fun loadMyStories(
        blog: UserModel,
        pin: PinModel?,
        model: Model,
        offset: Int = 0,
    ): List<StoryModel> {
        val limit = MAIN_PAGE_SIZE
        val stories = storyService.search(
            pin = pin,
            request = SearchStoryRequest(
                userIds = listOf(blog.id),
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.recommended,
                limit = limit,
                offset = offset,
                sortOrder = SortOrder.descending,
            ),
        )

        val result = pinStory(stories, pin?.storyId)
        model.addAttribute("myStories", mapper.setImpressions(result))

        if (result.size >= limit) {
            val nextOffset = offset + limit
            model.addAttribute("moreUrl", "/@/${blog.name}/my-stories?offset=$nextOffset")
            model.addAttribute("nextOffset", nextOffset)
            model.addAttribute("offset", offset)
        }
        return result
    }

    private fun loadFollowingStories(
        followingUserIds: List<Long>,
        model: Model,
        limit: Int,
    ): List<StoryModel> {
        // Find following users
        if (followingUserIds.isEmpty()) {
            return emptyList()
        }

        // Find stories from following users
        val followingStories = storyService.search(
            request = SearchStoryRequest(
                userIds = followingUserIds,
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.recommended,
                limit = limit,
                context = storyService.createSearchContext(),
            ),
        )
        model.addAttribute("followingStories", mapper.setImpressions(followingStories))
        return followingStories
    }

    private fun loadLatestStories(blog: UserModel, followingUserIds: List<Long>, model: Model): List<StoryModel> {
        val stories = mutableListOf<StoryModel>()
        storyService.search(
            SearchStoryRequest(
                status = StoryStatus.published,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = 50,
                context = storyService.createSearchContext(),
                dedupUser = true,
            ),
        )
            .filter { it.user.id != blog.id && !followingUserIds.contains(it.user.id) }

        model.addAttribute("latestStories", mapper.setImpressions(stories.take(5)))
        return stories
    }

    private fun pinStory(stories: List<StoryModel>, pinnedStoryId: Long?): List<StoryModel> {
        val pinnedStory = stories.find { it.id == pinnedStoryId }
            ?: return stories

        val result = mutableListOf<StoryModel>()
        result.add(pinnedStory)
        result.addAll(stories.filter { it.id != pinnedStory.id })
        return result
    }

    private fun loadReader(followingUserIds: List<Long>, blog: UserModel, model: Model): String {
        loadFollowingStories(followingUserIds, model, 50)
        val stories = loadLatestStories(blog, followingUserIds, model)

        model.addAttribute("page", getPage(blog, stories))
        return "page/blog/reader"
    }

    private fun loadPin(blog: UserModel, model: Model): PinModel? {
        if (!requestContext.toggles().pin) {
            return null
        }

        val pin = pinService.get(blog)
        model.addAttribute("pin", pin)
        return pin
    }

    private fun shouldShowFollowButton(blog: UserModel, model: Model) {
        model.addAttribute("showFollowButton", followerService.canFollow(blog.id))
    }

    private fun shouldShowCreateStory(blog: UserModel, stories: List<StoryModel>, model: Model) {
        if (stories.isNotEmpty() || !blog.blog || blog.id != requestContext.currentUser()?.id) {
            return
        }

        val count = storyService.count()
        model.addAttribute("showCreateStoryButton", count == 0)
    }

    protected fun getPage(user: UserModel, stories: List<StoryModel>) = createPage(
        name = pageName(),
        title = user.fullName,
        description = if (user.biography == null) "" else user.biography,
        type = "profile",
        url = url(user),
        imageUrl = user.pictureUrl,
        schemas = schemas.generate(user),
        rssUrl = "${user.slug}/rss",
        preloadImageUrls = stories.map { it.thumbnailLargeUrl }.filter { !it.isNullOrBlank() }.take(1) as List<String>,
    )
}
