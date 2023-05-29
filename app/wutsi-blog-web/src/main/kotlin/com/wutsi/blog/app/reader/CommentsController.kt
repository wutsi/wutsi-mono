package com.wutsi.blog.app.reader

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.form.CreateCommentForm
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
        @RequestParam(required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(required = false, defaultValue = "0") offset: Int = 0,
        model: Model,
    ): String {
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
