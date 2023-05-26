package com.wutsi.blog.app.common.controller

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.platform.core.error.exception.ConflictException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ModelAttribute
import java.util.UUID

abstract class AbstractPageController(
    @ModelAttribute(ModelAttributeName.REQUEST_CONTEXT) protected val requestContext: RequestContext,
) {
    @Value("\${wutsi.application.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${wutsi.application.server-url}")
    protected lateinit var baseUrl: String

    @Value("\${wutsi.google.ga.code}")
    protected lateinit var googleAnalyticsCode: String

    @Value("\${wutsi.facebook.pixel.code}")
    protected lateinit var facebookPixelId: String

    @Value("\${wutsi.facebook.app-id}")
    protected lateinit var facebookAppId: String

    @Value("\${wutsi.oauth.google.client-id}")
    protected lateinit var googleClientId: String

    protected abstract fun pageName(): String

    @ModelAttribute(ModelAttributeName.USER)
    fun getUser() = requestContext.currentUser()

    @ModelAttribute(ModelAttributeName.SUPER_USER)
    fun getSuperUser() = requestContext.currentSuperUser()

    @ModelAttribute(ModelAttributeName.TOGGLES)
    fun getToggles() = requestContext.toggles()

    @ModelAttribute(ModelAttributeName.PAGE)
    fun getPage() = page()

    @ModelAttribute(ModelAttributeName.HITID)
    fun getHitId() = UUID.randomUUID().toString()

    open fun shouldBeIndexedByBots() = false

    open fun shouldShowGoogleOneTap() = false

    protected fun getPageRobotsHeader() = if (shouldBeIndexedByBots()) "index,follow" else "noindex,nofollow"

    open fun page() = createPage(
        title = requestContext.getMessage("page.home.metadata.title"),
        description = requestContext.getMessage("page.home.metadata.description"),
    )

    protected fun createPage(
        name: String = pageName(),
        title: String,
        description: String,
        type: String = "website",
        imageUrl: String? = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
        schemas: String? = null,
        url: String? = null,
        author: String? = null,
        publishedTime: String? = null,
        modifiedTime: String? = null,
        twitterUserId: String? = null,
        canonicalUrl: String? = null,
        tags: List<String> = emptyList(),
        rssUrl: String? = null,
        preloadImageUrls: List<String> = emptyList(),
    ) = PageModel(
        name = name,
        title = title,
        description = description,
        type = type,
        url = url,
        author = author,
        publishedTime = publishedTime,
        modifiedTime = modifiedTime,
        twitterUserId = twitterUserId,
        canonicalUrl = canonicalUrl,
        schemas = schemas,
        tags = tags,
        robots = getPageRobotsHeader(),
        baseUrl = baseUrl,
        assetUrl = assetUrl,
        googleAnalyticsCode = this.googleAnalyticsCode,
        facebookAppId = this.facebookAppId,
        facebookPixelCode = this.facebookPixelId,
        googleClientId = this.googleClientId,
        showGoogleOneTap = shouldShowGoogleOneTap(),
        language = LocaleContextHolder.getLocale().language,
        imageUrl = imageUrl,
        rssUrl = rssUrl,
        preloadImageUrls = preloadImageUrls,
    )

    protected fun url(story: StoryModel) = baseUrl + story.slug

    protected fun url(user: UserModel) = baseUrl + user.slug

    protected fun errorKey(ex: Exception): String {
        if (ex is ConflictException) {
            val message = ex.message
            if (
                message == "duplicate_name" ||
                message == "duplicate_email" ||
                message == "syndicate_error" ||
                message == "publish_error" ||
                message == "story_already_imported" ||
                message == "title_missing" ||
                message == "duplicate_mobile_number" ||
                message == "permission_denied"
            ) {
                return "error.$message"
            }
        }
        return "error.unexpected"
    }
}
