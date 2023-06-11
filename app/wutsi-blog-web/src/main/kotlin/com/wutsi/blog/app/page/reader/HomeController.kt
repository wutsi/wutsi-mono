package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder.DESCENDING
import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.reader.view.StoryRssView
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
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
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun page() = createPage(
        title = requestContext.getMessage("page.home.metadata.title"),
        description = requestContext.getMessage("page.home.metadata.description"),
        schemas = schemas.generate(),
        rssUrl = "/rss",
    )

    @GetMapping
    fun index(model: Model): String {
        val writers = userService.search(
            SearchUserRequest(
                blog = true,
                limit = 10,
                active = true,
                sortBy = UserSortStrategy.POPULARITY,
                sortOrder = DESCENDING,
            ),
        )

        model.addAttribute("writers", writers)
        return "reader/home"
    }

    @GetMapping("/rss")
    fun index(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date? = null,
    ): StoryRssView =
        StoryRssView(
            baseUrl = baseUrl,
            endDate = endDate,
            startDate = startDate,
            storyService = storyService,
        )
}
