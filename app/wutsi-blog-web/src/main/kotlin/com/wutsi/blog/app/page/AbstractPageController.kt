package com.wutsi.blog.app.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.i18n.phonenumbers.NumberParseException
import com.wutsi.blog.app.exception.MobilePaymentNotSupportedForCountryException
import com.wutsi.blog.app.model.AdsModel
import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoreService
import com.wutsi.blog.app.service.WalletService
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.ErrorResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException
import java.util.UUID

abstract class AbstractPageController(
    protected val requestContext: RequestContext,
) {
    @Autowired
    protected lateinit var storeService: StoreService

    @Autowired
    protected lateinit var walletService: WalletService

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

    @ModelAttribute(ModelAttributeName.REQUEST_CONTEXT)
    fun getReqContext() = requestContext

    @ModelAttribute(ModelAttributeName.ADS_BANNER)
    open fun getAdsBanner(): AdsModel? {
        if (!requestContext.toggles().adsBanner) {
            return null
        }

        return null
//        return AdsModel(
//            id = "best-talent-cm",
//            imageUrl = "$assetUrl/assets/wutsi/img/ads/best-talent-cm/banner.png",
//            mobileImageUrl = "$assetUrl/assets/wutsi/img/ads/best-talent-cm/banner-mobile.png",
//            url = "https://btc4.dotchoize.com",
//            title = "Best Talent Cameroon"
//        )
    }

    open fun shouldBeIndexedByBots() = false

    open fun shouldShowGoogleOneTap() = false

    private fun getPageRobotsHeader() = if (shouldBeIndexedByBots()) {
        "index,follow"
    } else {
        "noindex,nofollow"
    }

    protected fun getWallet(blog: UserModel): WalletModel? =
        blog.walletId?.let { walletId ->
            walletService.get(walletId)
        }

    protected fun getStore(blog: UserModel): StoreModel? =
        storeService.get(blog)

    open fun page() = createPage(
        title = requestContext.getMessage("page.home.metadata.title"),
        description = requestContext.getMessage("page.home.metadata.description"),
    )

    protected fun redirectTo(returnUrl: String?, from: String?): String =
        if (returnUrl == null) {
            "redirect:/"
        } else {
            val separator = if (returnUrl.contains("?")) {
                "&"
            } else {
                "?"
            }

            "redirect:$returnUrl${separator}utm_from=$from"
        }

    protected fun createPage(
        name: String = pageName(),
        title: String,
        description: String,
        type: String = "website",
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
        robots: String? = null,
        imageUrl: String? = null,
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
        robots = robots ?: getPageRobotsHeader(),
        baseUrl = baseUrl,
        assetUrl = assetUrl,
        googleAnalyticsCode = this.googleAnalyticsCode,
        facebookAppId = this.facebookAppId,
        facebookPixelCode = this.facebookPixelId,
        googleClientId = this.googleClientId,
        showGoogleOneTap = shouldShowGoogleOneTap(),
        language = LocaleContextHolder.getLocale().language,
        rssUrl = rssUrl,
        preloadImageUrls = preloadImageUrls,
        imageUrl = imageUrl,
    )

    protected fun url(story: StoryModel) = baseUrl + story.slug

    protected fun url(user: UserModel) = baseUrl + user.slug

    protected fun errorKey(ex: Exception): String {
        if (ex is HttpClientErrorException) {
            val code = getErrorResponse(ex)?.error?.code
            if (
                code == ErrorCode.USER_NAME_DUPLICATE ||
                code == ErrorCode.USER_EMAIL_DUPLICATE ||
                code == ErrorCode.STORY_ALREADY_IMPORTED ||
                code == ErrorCode.PERMISSION_DENIED
            ) {
                return "error.$code"
            }
        }
        return "error.unexpected"
    }

    private fun getErrorResponse(ex: HttpClientErrorException): ErrorResponse? =
        try {
            ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }

    protected fun resolveReferer(returnUrl: String?): String? =
        if (returnUrl?.startsWith("/read/") == true) {
            "story"
        } else if (returnUrl?.startsWith("/product/") == true) {
            "product"
        } else if (returnUrl?.endsWith("/shop") == true) {
            "shop"
        } else if (returnUrl?.contains("/@/") == true) {
            "blog"
        } else {
            null
        }

    protected fun extractIdFromSlug(returnUrl: String?, prefix: String): Long? {
        if (returnUrl?.startsWith(prefix) == true) {
            val i = prefix.length
            val j = returnUrl.indexOf("/", i)
            val id = if (j < 0) {
                returnUrl.substring(i)
            } else {
                returnUrl.substring(i, j)
            }
            return try {
                id.toLong()
            } catch (ex: Exception) {
                null
            }
        }
        return null
    }

    protected fun toErrorKey(ex: Exception): String =
        if (ex is MobilePaymentNotSupportedForCountryException) {
            "error.invalid_phone_number"
        } else if (ex is NumberParseException) {
            "error.invalid_phone_number"
        } else if (ex is HttpClientErrorException) {
            try {
                val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
                val key = "error." + response.error.code
                requestContext.getMessage(key) // Check if the key exist
                key
            } catch (e: Exception) {
                "error.unexpected"
            }
        } else {
            "error.unexpected"
        }
}
