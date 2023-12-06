package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.page.reader.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/writers")
class WritersController(
    private val schemas: WutsiSchemasGenerator,
    private val userService: UserService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LIMIT = 50
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
        val writers = userService.trending(LIMIT)
        if (writers.isNotEmpty()) {
            model.addAttribute("writers", writers)
        }
        return "reader/fragment/writers"
    }
}
