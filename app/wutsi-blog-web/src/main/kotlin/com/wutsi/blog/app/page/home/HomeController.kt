package com.wutsi.blog.app.page.home

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder.descending
import com.wutsi.blog.client.story.SearchStoryContext
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy.published
import com.wutsi.blog.client.story.StorySortStrategy.recommended
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UserSortStrategy.last_publication
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Date

@Controller
@RequestMapping("/")
class HomeController(
    private val schemas: WutsiSchemasGenerator,
    private val userService: UserService,
    private val storyService: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun page() = createPage(
        title = requestContext.getMessage("page.home.metadata.title"),
        description = requestContext.getMessage("page.home.metadata.description"),
        schemas = schemas.generate(),
        showNotificationOptIn = true,
        rssUrl = "/rss",
    )

    @GetMapping
    fun index(model: Model): String {
        // Writers
        val writers = userService.search(
            SearchUserRequest(
                blog = true,
                limit = 3,
                sortBy = last_publication,
                sortOrder = descending,
            ),
        )
        model.addAttribute("writers", writers)

        // Recent
        val recent = storyService.search(
            request = SearchStoryRequest(
                sortBy = published,
                limit = 10,
                publishedStartDate = DateUtils.addDays(Date(), -7),
                context = SearchStoryContext(
                    userId = requestContext.currentUser()?.id,
                    deviceId = requestContext.deviceId(),
                ),
                dedupUser = true,
            ),
        ).take(5)
        model.addAttribute("recentStories", recent)

        // Recommendations
        val recentIds = recent.map { it.id }
        val recommended = storyService.search(
            request = SearchStoryRequest(
                sortBy = recommended,
                limit = 50,
                context = SearchStoryContext(
                    userId = requestContext.currentUser()?.id,
                    deviceId = requestContext.deviceId(),
                ),
                dedupUser = true,
            ),
        ).filter { !recentIds.contains(it.id) }.take(10)
        model.addAttribute("recommendedStories", recommended)

        return "page/home/index"
    }
}
