package com.wutsi.blog.app.page.editor

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.story.AbstractStoryController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class EditorShareController(
    service: StoryService,
    requestContext: RequestContext,

    @Value("\${wutsi.application.server-url}") private val websiteUrl: String,
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR_SHARE

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/share")
    fun index(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val story = getStory(id)

        model.addAttribute("story", story)
        model.addAttribute("storyUrl", "${websiteUrl}${story.slug}")
        model.addAttribute("page", toPage(story))
        loadChannels(story, model)
        return "page/editor/share"
    }

    fun loadChannels(story: StoryModel, model: Model) {
        if (!story.publishToSocialMedia) {
            return
        }

//        try {
//            val all = channels.all()
//            model.addAttribute("channelTwitter", all.find { it.type == ChannelType.twitter && it.connected })
//            model.addAttribute("channelFacebook", all.find { it.type == ChannelType.facebook && it.connected })
//            model.addAttribute("channelLinkedin", all.find { it.type == ChannelType.linkedin && it.connected })
//            model.addAttribute("channelTelegram", all.find { it.type == ChannelType.telegram && it.connected })
//        } catch (ex: Exception) {
//        }
    }

    protected fun toPage(story: StoryModel) = createPage(
        name = pageName(),
        title = story.title,
        description = story.summary,
        url = url(story),
        imageUrl = story.thumbnailUrl,
        author = story.user.fullName,
        publishedTime = story.publishedDateTimeISO8601,
        modifiedTime = story.modificationDateTimeISO8601,
        tags = story.tags.map { it.name },
        twitterUserId = story.user.twitterId,
        canonicalUrl = story.sourceUrl,
    )
}
