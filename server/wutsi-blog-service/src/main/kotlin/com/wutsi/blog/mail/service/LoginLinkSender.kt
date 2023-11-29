package com.wutsi.blog.mail.service

import com.wutsi.blog.account.dto.LoginLinkCreatedEventPayload
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Service
class LoginLinkSender(
    private val smtp: SMTPSender,
    private val templateEngine: TemplateEngine,
    private val mailFilterSet: MailFilterSet,
    private val messages: MessageSource,
    private val eventStore: EventStore,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
    @Value("\${wutsi.application.mail.login-link.ses-configuration-set}") private val sesConfigurationSet: String,
) {
    fun send(eventId: String): String? {
        val event = eventStore.event(eventId)
        val message = createEmailMessage(event)
        return smtp.send(message)
    }

    private fun createEmailMessage(event: Event): Message {
        val payload = event.payload as LoginLinkCreatedEventPayload
        return Message(
            recipient = Party(email = payload.email),
            language = payload.language,
            mimeType = "text/html;charset=UTF-8",
            data = mapOf(),
            subject = messages.getMessage("login_link.subject", emptyArray(), Locale(payload.language)),
            body = generateBody(event, payload),
            headers = mapOf(
                "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
            )
        )
    }

    private fun generateBody(event: Event, payload: LoginLinkCreatedEventPayload): String {
        var linkUrl = "$webappUrl/login/email/callback?link-id=${event.id}"

        val thymleafContext = Context(Locale(payload.language))
        thymleafContext.setVariable("linkUrl", linkUrl)

        val body = templateEngine.process("mail/login-link.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = createMailContext(payload),
        )
    }

    private fun createMailContext(payload: LoginLinkCreatedEventPayload) = MailContext(
        assetUrl = assetUrl,
        websiteUrl = webappUrl,
        template = "wutsi",
        blog = Blog(
            name = null,
            fullName = "Wutsi",
            language = payload.language,
            logoUrl = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
        ),
    )
}
