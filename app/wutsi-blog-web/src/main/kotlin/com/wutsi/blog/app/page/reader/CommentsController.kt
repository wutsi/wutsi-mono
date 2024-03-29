package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.form.CreateCommentForm
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.CommentService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/comments")
class CommentsController(
    private val service: CommentService,
    private val stories: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    override fun pageName() = PageName.COMMENT

    @GetMapping
    fun index(@RequestParam(name = "story-id") storyId: Long, model: Model): String {
        val story = stories.get(storyId)
        model.addAttribute("story", story)
        list(storyId, model = model)
        return "reader/comments"
    }

    @ResponseBody
    @PostMapping(produces = ["application/json"], consumes = ["application/json"])
    fun submit(@RequestBody form: CreateCommentForm): Map<String, String> {
        service.create(form)
        return mapOf("success" to "true")
    }

    @GetMapping("/list")
    fun list(
        @RequestParam(name = "story-id") storyId: Long,
        @RequestParam(name = "limit", required = false) paramLimit: Int? = null,
        @RequestParam(required = false, defaultValue = "0") offset: Int = 0,
        model: Model,
    ): String {
        val limit = paramLimit ?: LIMIT
        val items = service.search(storyId, limit, offset)
        model.addAttribute("comments", items)

        if (items.size >= limit) {
            val nextOffset = offset + limit
            model.addAttribute("moreUrl", "/comments/list?story-id=$storyId&offset=$nextOffset")
            model.addAttribute("nextOffset", nextOffset)
            model.addAttribute("offset", offset)
        }

        return "reader/fragment/comments"
    }
}
