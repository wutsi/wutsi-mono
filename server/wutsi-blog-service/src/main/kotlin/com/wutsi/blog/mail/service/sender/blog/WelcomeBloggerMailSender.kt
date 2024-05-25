package com.wutsi.blog.mail.service.sender.blog

import com.wutsi.blog.mail.service.sender.AbstractWutsiMailSender
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import java.util.Locale

@Service
class WelcomeBloggerMailSender(
    @Value("\${wutsi.application.mail.welcome-blogger.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractWutsiMailSender() {
    fun send(blog: UserEntity): Boolean {
        val message = createEmailMessage(blog)
        val messageId = smtp.send(message)
        return messageId != null
    }

    private fun createEmailMessage(blog: UserEntity): Message {
        val language = getLanguage(blog)
        return Message(
            recipient = Party(
                email = blog.email ?: "",
                displayName = blog.fullName,
            ),
            language = language,
            mimeType = "text/html;charset=UTF-8",
            data = mapOf(),
            subject = messages.getMessage("welcome-blogger.subject", emptyArray(), Locale(language)),
            body = generateBody(blog, language),
            headers = mapOf(
                "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
            )
        )
    }

    private fun generateBody(
        blog: UserEntity,
        language: String,
    ): String {
        val mailContext = createMailContext(blog.fullName, language)

        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("blog", mailContext.blog)
        thymleafContext.setVariable("assetUrl", assetUrl)
        val body = templateEngine.process("mail/welcome-blogger.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }
}
