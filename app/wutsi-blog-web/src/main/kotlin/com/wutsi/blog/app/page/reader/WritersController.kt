package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
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
    )

    @GetMapping()
    fun index(model: Model): String {
        val writers = userService.search(
            SearchUserRequest(
                blog = true,
                limit = 20,
                sortBy = UserSortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
            ),
        )

        model.addAttribute("writers", writers)
        return "reader/writers"
    }
}
