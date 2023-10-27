package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Controller
@RequestMapping("/writers")
class WritersController(
    private val schemas: WutsiSchemasGenerator,
    private val userService: UserService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LIMIT = 20
    }

    override fun pageName() = PageName.WRITERS

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun page() = createPage(
        title = requestContext.getMessage("page.writers.metadata.title"),
        description = requestContext.getMessage("page.writers.metadata.description"),
        schemas = schemas.generate(),
    )

    @GetMapping()
    fun index(model: Model): String {
        more(0, model)
        return "reader/writers"
    }

    @GetMapping("/more")
    fun more(@RequestParam("offset") offset: Int, model: Model): String {
        val writers = userService.search(
            SearchUserRequest(
                blog = true,
                offset = offset,
                limit = LIMIT,
                sortBy = UserSortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                minPublishStoryCount = WPPConfig.MIN_STORY_COUNT,
                minCreationDateTime = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS)
            ),
        )
        if (writers.isNotEmpty()) {
            model.addAttribute("writers", writers)
            if (writers.size >= LIMIT) {
                model.addAttribute("moreUrl", "/writers/more?offset=" + (offset + LIMIT))
            }
        }
        return "reader/fragment/writers"
    }
}
