package com.wutsi.blog.mail.service.sender

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.model.BlogModel

abstract class AbstractWutsiMailSender : AbstractMailSender() {
    companion object {
        const val TEMPLATE = "wutsi"
    }

    protected fun createMailContext(fullName: String, language: String) = MailContext(
        assetUrl = assetUrl,
        websiteUrl = webappUrl,
        template = TEMPLATE,
        blog = BlogModel(
            name = null,
            fullName = fullName,
            language = language,
            logoUrl = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
            biography = null
        ),
    )
}
