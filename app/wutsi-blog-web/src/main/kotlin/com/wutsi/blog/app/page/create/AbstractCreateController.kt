package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.backend.IpApiBackend
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.country.dto.Country
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractCreateController(
    protected val userService: UserService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    @Autowired
    protected lateinit var ip: IpApiBackend

    @Autowired
    protected lateinit var logger: KVLogger

    abstract fun pagePath(): String

    abstract fun redirectUrl(): String

    abstract fun attributeName(): String

    abstract fun value(): String?

    override fun page() = createPage(
        title = requestContext.getMessage("page.create.metadata.title"),
        description = requestContext.getMessage("page.create.metadata.description"),
    )

    @GetMapping
    open fun index(model: Model): String {
        if (isEnabledInCountry()) {
            val value = value()
            model.addAttribute("value", value)
            return pagePath()
        } else {
            return "create/not-supported"
        }
    }

    @GetMapping("/submit")
    fun submit(
        @RequestParam(required = false) value: String? = null,
        model: Model,
    ): String {
        try {
            doSubmit(value)
            return "redirect:" + redirectUrl()
        } catch (ex: Exception) {
            val error = errorKey(ex)
            model.addAttribute("error", requestContext.getMessage(error))
            model.addAttribute("value", value)
            return pagePath()
        }
    }

    protected open fun doSubmit(value: String?) {
        userService.updateAttribute(
            UserAttributeForm(
                name = attributeName(),
                value = toValue(value),
            ),
        )
    }

    protected open fun toValue(value: String?) = value

    protected fun getCountry(): String? = try {
        ip.resolve(requestContext.remoteIp()).countryCode
    } catch (ex: Exception) {
        LoggerFactory.getLogger(this::class.java).warn("Unable to resolve the country", ex)
        null
    }

    protected fun isEnabledInCountry(): Boolean {
        logger.add("blog_country_restriction", getToggles().blogCountryRestriction)
        if (getToggles().blogCountryRestriction) {
            val country = getCountry() ?: return false

            logger.add("country", country)
            return Country.all.find { item -> item.code.equals(country, true) } != null
        } else {
            return true
        }
    }
}
