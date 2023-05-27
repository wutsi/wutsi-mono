package com.wutsi.blog.app.reader

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.story.AbstractStoryReadController
import com.wutsi.blog.app.reader.schemas.StorySchemasGenerator
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.service.LikeService
import com.wutsi.blog.app.service.PinService
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

@Controller
class ReadController(
    private val schemas: StorySchemasGenerator,
    private val likeService: LikeService,
    private val pinService: PinService,

    followerService: FollowerService,
    ejsJsonReader: EJSJsonReader,
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryReadController(ejsJsonReader, followerService, service, requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReadController::class.java)
    }

    override fun pageName() = PageName.READ

    override fun requiredPermissions() = listOf(Permission.reader)

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun generateSchemas(story: StoryModel) = schemas.generate(story)

    @GetMapping("/read/{id}/{title}")
    fun read(
        @PathVariable id: Long,
        @PathVariable title: String,
        @RequestParam(required = false) comment: String? = null,
        @RequestParam(required = false) translate: String? = null,
        model: Model,
        response: HttpServletResponse,
    ): String {
        return read(id, comment, translate, model, response)
    }

    @GetMapping("/read/{id}")
    fun read(
        @PathVariable id: Long,
        @RequestParam(required = false) comment: String? = null,
        @RequestParam(required = false) translate: String? = null,
        model: Model,
        response: HttpServletResponse,
    ): String {
        if (requestContext.toggles().translation) {
            if (!supportsLanguage(translate)) {
                throw NotFoundException(Error("language_not_supported"))
            }

            val story = loadPage(id, model, translate)
            loadTranslationInfo(translate, story, model)
            shouldShowFollowButton(story, model)
        } else {
            loadPage(id, model, null)
        }
        return "reader/read"
    }

    @ResponseBody
    @GetMapping("/read/{id}/like")
    fun like(@PathVariable id: Long) {
        likeService.like(id)
    }

    @ResponseBody
    @GetMapping("/read/{id}/unlike")
    fun unlike(@PathVariable id: Long) {
        likeService.unlike(id)
    }

    @ResponseBody
    @GetMapping("/read/{id}/pin")
    fun pin(@PathVariable id: Long) {
        pinService.pin(id)
    }

    @ResponseBody
    @GetMapping("/read/{id}/unpin")
    fun unpin(@PathVariable id: Long) {
        pinService.unpin(id)
    }

    @GetMapping("/read/{id}/recommend")
    fun recommend(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "summary") layout: String = "summary",
        model: Model,
    ): String {
        try {
            val stories = service.recommend(id, 20)
            model.addAttribute("stories", stories.take(5))
            if (stories.isNotEmpty()) {
                val story = service.get(id)
                model.addAttribute("blog", story.user)
                model.addAttribute("layout", layout)
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find Story recommendations", ex)
        }
        return "reader/fragment/recommend"
    }

    override fun shouldCheckAccess(): Boolean = true

    private fun loadTranslationInfo(translate: String?, story: StoryModel, model: Model) {
        if (!requestContext.toggles().translation) {
            return
        }

        if (translate == null) {
            if (!supportsLanguage(story.language)) {
                return
            }

            val userLocale = LocaleContextHolder.getLocale()
            if (userLocale.language != story.language && supportsLanguage(userLocale.language)) {
                model.addAttribute("showTranslation", true)
                model.addAttribute("translationUrl", "${story.slug}?translate=${userLocale.language}")
                model.addAttribute(
                    "translationText",
                    requestContext.getMessage(
                        key = "label.read_story_translation",
                        args = arrayOf(userLocale.getDisplayLanguage(userLocale)),
                        locale = userLocale,
                    ),
                )
            }
        } else {
            val original = getStory(story.id)
            model.addAttribute("showTranslation", true)
            model.addAttribute("translationOriginalUrl", original.slug)
            model.addAttribute("translationOriginalTitle", original.title)
        }
    }

    private fun shouldShowFollowButton(story: StoryModel, model: Model) {
        val showButton = followerService.canFollow(story.user.id)
        model.addAttribute("showFollowButton", showButton)
        if (showButton) {
            model.addAttribute("followMessage", requestContext.getMessage("page.read.follow_blog"))
        }
    }

    private fun supportsLanguage(language: String?): Boolean =
        language == null || requestContext.supportsLanguage(language)
}
