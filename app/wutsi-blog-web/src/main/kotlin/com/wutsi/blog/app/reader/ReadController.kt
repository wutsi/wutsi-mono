package com.wutsi.blog.app.reader

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.StorySchemasGenerator
import com.wutsi.blog.app.page.story.AbstractStoryReadController
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.service.LikeService
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
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

    followerService: FollowerService,
    ejsJsonReader: EJSJsonReader,
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryReadController(ejsJsonReader, followerService, service, requestContext) {

    override fun pageName() = PageName.READ

    override fun requiredPermissions() = listOf(Permission.reader)

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun generateSchemas(story: StoryModel) = schemas.generate(story)

    override fun showNotificationOptIn(): Boolean = true

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
        return "page/story/read"
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
