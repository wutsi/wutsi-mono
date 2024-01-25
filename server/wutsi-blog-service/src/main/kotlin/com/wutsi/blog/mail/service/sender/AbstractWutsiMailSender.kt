package com.wutsi.blog.mail.service.sender

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilterSet
import com.wutsi.blog.mail.service.model.BlogModel
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.thymeleaf.TemplateEngine

abstract class AbstractWutsiMailSender {
    companion object {
        const val TEMPLATE = "wutsi"
    }

    @Autowired
    protected lateinit var smtp: SMTPSender

    @Autowired
    protected lateinit var templateEngine: TemplateEngine

    @Autowired
    protected lateinit var mailFilterSet: MailFilterSet

    @Autowired
    protected lateinit var messages: MessageSource

    @Value("\${wutsi.application.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${wutsi.application.website-url}")
    protected lateinit var webappUrl: String

    protected fun createMailContext(fullName: String, language: String) = MailContext(
        assetUrl = assetUrl,
        websiteUrl = webappUrl,
        template = TEMPLATE,
        blog = BlogModel(
            name = null,
            fullName = fullName,
            language = language,
            logoUrl = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
        ),
    )

    protected fun getLanguage(recipient: UserEntity): String =
        recipient.language ?: "en"
}
