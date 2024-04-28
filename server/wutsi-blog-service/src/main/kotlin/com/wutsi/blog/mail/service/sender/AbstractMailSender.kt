package com.wutsi.blog.mail.service.sender

import com.wutsi.blog.mail.service.MailFilterSet
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.thymeleaf.TemplateEngine

abstract class AbstractMailSender {
    companion object {
        const val ADS_USER_AGENT = "Wutsi-Mail-Sender/1.0"
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


    protected fun getLanguage(recipient: UserEntity): String =
        recipient.language ?: "en"
}
