package com.wutsi.blog.app.page.home

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UserSortStrategy
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/writers")
class WritersController(
    private val schemas: WutsiSchemasGenerator,
    private val userService: UserService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.WRITERS

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun page() = createPage(
        title = requestContext.getMessage("page.writers.metadata.title"),
        description = requestContext.getMessage("page.writers.metadata.description"),
        schemas = schemas.generate(),
        showNotificationOptIn = true,
    )

    @GetMapping()
    fun index(model: Model): String {
        val writers = userService.search(
            SearchUserRequest(
                blog = true,
                limit = 20,
                sortBy = UserSortStrategy.last_publication,
                sortOrder = SortOrder.descending,
            ),
        ).filter {
            it.storyCount > 0
        }

        model.addAttribute("writers", writers)
        return "page/home/writers"
    }
}
