package com.wutsi.blog.mail.service.sender.blog

import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import java.util.Locale

@Service
class WelcomeSubscriberMailSender(
    private val linkMapper: LinkMapper,

    @Value("\${wutsi.application.mail.welcome-subscriber.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    fun send(
        blog: UserEntity,
        recipient: UserEntity,
        stories: List<StoryEntity>,
    ): Boolean {
        val message = createEmailMessage(blog, recipient, stories)
        val messageId = smtp.send(message)
        return messageId != null
    }

    private fun createEmailMessage(
        blog: UserEntity,
        recipient: UserEntity,
        stories: List<StoryEntity>,
    ): Message {
        val language = getLanguage(recipient)
        return Message(
            sender = Party(
                displayName = blog.fullName,
                email = blog.email ?: "",
            ),
            recipient = Party(
                email = recipient.email ?: "",
                displayName = recipient.fullName,
            ),
            language = language,
            mimeType = "text/html;charset=UTF-8",
            data = mapOf(),
            subject = messages.getMessage("welcome-subscriber.subject", emptyArray(), Locale(language)),
            body = generateBody(blog, recipient, stories, language),
            headers = mapOf(
                "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
            )
        )
    }

    private fun generateBody(
        blog: UserEntity,
        recipient: UserEntity,
        stories: List<StoryEntity>,
        language: String,
    ): String {
        val mailContext = createMailContext(blog, recipient)

        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("blog", mailContext.blog)
        if (stories.isNotEmpty()) {
            thymleafContext.setVariable(
                "stories",
                stories.map { story -> linkMapper.toLinkModel(story, mailContext, blog) }
            )
        }

        val body = templateEngine.process("mail/welcome-subscriber.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }
}
