package com.wutsi.blog.app.page.editor

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.TopicModel
import com.wutsi.blog.app.page.editor.model.PublishForm
import com.wutsi.blog.app.page.story.AbstractStoryController
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.TopicService
import com.wutsi.blog.app.util.PageName
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.Date

@Controller
class EditorTagController(
    private val topicService: TopicService,
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR_TAG

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/tag")
    fun index(
        @PathVariable id: Long,
        @RequestParam(required = false) error: String? = null,
        model: Model,
    ): String {
        val story = getStory(id)

        model.addAttribute("story", story)
        model.addAttribute("error", error)
        loadTopics(model)
        loadScheduledPublishDate(story, model)

        return "page/editor/tag"
    }

    private fun loadTopics(model: Model) {
        val topics = topicService.all()
            .filter { it.parentId != -1L }
            .map {
                val parent = topicService.get(it.parentId)
                TopicModel(
                    id = it.id,
                    name = it.name,
                    displayName = if (parent == null) it.displayName else parent.displayName + " / " + it.displayName,
                    parentId = it.parentId,
                )
            }

        model.addAttribute("topics", topics)
    }

    private fun loadScheduledPublishDate(story: StoryModel, model: Model) {
        if (story.published) {
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val tomorrow = DateUtils.addDays(Date(), 1)
        model.addAttribute("minScheduledPublishDate", dateFormat.format(tomorrow))
        model.addAttribute(
            "scheduledPublishDate",
            story.scheduledPublishDateTimeAsDate?.let { dateFormat.format(it) } ?: "",
        )
        model.addAttribute("publishNow", story.scheduledPublishDateTimeAsDate == null)
    }

    @GetMapping("/me/story/tag/submit")
    fun submit(@ModelAttribute editor: PublishForm): String {
        try {
            service.publish(editor)
            return if (editor.publishNow) {
                "redirect:/me/story/${editor.id}/share"
            } else {
                "redirect:/me/draft"
            }
        } catch (ex: Exception) {
            return "redirect:/me/story/${editor.id}/tag?error=publish_error"
        }
    }
}
