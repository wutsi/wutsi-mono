package com.wutsi.blog.app.page

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.Format
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model

abstract class AbstractStoryReadController(
    private val ejsJsonReader: EJSJsonReader,

    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryController(service, requestContext) {

    @Autowired
    private lateinit var imageService: ImageService

    protected fun loadPage(id: Long, model: Model): StoryModel {
        val story = getStory(id)
        return loadPage(story, model)
    }

    protected fun loadPage(story: StoryModel, model: Model, summary: Boolean = false): StoryModel {
        model.addAttribute("story", story)

        val page = toPage(story)
        model.addAttribute(ModelAttributeName.PAGE, page)
        loadContent(story, model, summary)
        return story
    }

    protected open fun generateSchemas(story: StoryModel): String? = null

    private fun loadContent(story: StoryModel, model: Model, summary: Boolean) {
        if (story.content == null) {
            return
        }

        val html = service.generateHtmlContent(story, summary)
        model.addAttribute("html", html)

        val ejs = ejsJsonReader.read(story.content)
        model.addAttribute("hasTwitterEmbed", hasEmbed(ejs, "twitter"))
        model.addAttribute("hasYouTubeEmbed", hasEmbed(ejs, "youtube"))
        model.addAttribute("hasVimeoEmbed", hasEmbed(ejs, "vimeo"))
        model.addAttribute("hasCode", hasCode(ejs))
        model.addAttribute("hasRaw", hasRaw(ejs))
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
        imageUrl = story.thumbnailUrl?.let {
            imageService.transform(
                story.thumbnailUrl,
                Transformation(
                    focus = Focus.FACE,
                    dimension = Dimension(width = 1200, height = 630),
                    format = Format.PNG,
                ),
            )
        },
        author = story.user.fullName,
        publishedTime = story.publishedDateTimeISO8601,
        modifiedTime = story.modificationDateTimeISO8601,
        tags = toPageTags(story),
        twitterUserId = story.user.twitterId,
        canonicalUrl = story.sourceUrl,
        schemas = generateSchemas(story),
        preloadImageUrls = story.thumbnailLargeUrl?.let { listOf(it) } ?: emptyList(),
    )

    private fun toPageTags(story: StoryModel): List<String> {
        val tags = mutableListOf<String>()
        tags.addAll(story.tags.map { tag -> tag.displayName })
        tags.add(story.category.longTitle)
        return tags
    }
}
