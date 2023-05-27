package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.ui.Model

abstract class AbstractStoryReadController(
    private val ejsJsonReader: EJSJsonReader,
    protected val followerService: FollowerService,

    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryController(service, requestContext) {

    protected fun loadPage(id: Long, model: Model): StoryModel {
        val story = getStory(id)
        model.addAttribute("story", story)

        val page = toPage(story)
        model.addAttribute(ModelAttributeName.PAGE, page)

        val fullAccess = if (shouldCheckAccess()) {
            hasFullAccess(story)
        } else {
            true
        }

        loadContent(story, model, fullAccess)
        return story
    }

    protected open fun shouldCheckAccess(): Boolean = false

    protected open fun hasFullAccess(story: StoryModel): Boolean = true

    protected open fun generateSchemas(story: StoryModel): String? = null

    private fun loadContent(story: StoryModel, model: Model, fullAccess: Boolean) {
        if (story.content == null) {
            return
        }

        val html = service.generateHtmlContent(story, !fullAccess)
        model.addAttribute("html", html)

        val ejs = ejsJsonReader.read(story.content)
        model.addAttribute("hasTwitterEmbed", hasEmbed(ejs, "twitter"))
        model.addAttribute("hasYouTubeEmbed", hasEmbed(ejs, "youtube"))
        model.addAttribute("hasVimeoEmbed", hasEmbed(ejs, "vimeo"))
        model.addAttribute("hasCode", hasCode(ejs))
        model.addAttribute("hasRaw", hasRaw(ejs))
        model.addAttribute("fullAccess", fullAccess)
    }

    private fun hasEmbed(doc: EJSDocument, service: String) = doc
        .blocks
        .find { it.type == BlockType.embed && it.data.service == service } != null

    private fun hasCode(doc: EJSDocument) = doc
        .blocks
        .find { it.type == BlockType.code } != null

    private fun hasRaw(doc: EJSDocument) = doc
        .blocks
        .find { it.type == BlockType.raw } != null

    protected fun toPage(story: StoryModel) = createPage(
        name = pageName(),
        title = story.title,
        description = story.summary,
        type = "article",
        url = url(story),
        imageUrl = story.thumbnailUrl,
        author = story.user.fullName,
        publishedTime = story.publishedDateTimeISO8601,
        modifiedTime = story.modificationDateTimeISO8601,
        tags = story.tags.map { it.name },
        twitterUserId = story.user.twitterId,
        canonicalUrl = story.sourceUrl,
        schemas = generateSchemas(story),
        preloadImageUrls = story.thumbnailLargeUrl?.let { listOf(it) } ?: emptyList(),
    )
}
