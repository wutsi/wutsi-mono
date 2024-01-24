package com.wutsi.blog.mail.service.sender

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilterSet
import com.wutsi.blog.mail.service.model.BlogModel
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.thymeleaf.TemplateEngine
import java.text.DecimalFormat

abstract class AbstractBlogMailSender {
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

    protected abstract fun getUnsubscribeUrl(blog: UserEntity, recipient: UserEntity): String?

    protected fun createMailContext(blog: UserEntity, recipient: UserEntity?, story: StoryEntity? = null): MailContext {
        return MailContext(
            storyId = story?.id,
            assetUrl = assetUrl,
            websiteUrl = webappUrl,
            template = "default",
            blog = BlogModel(
                name = blog.name,
                logoUrl = blog.pictureUrl,
                fullName = blog.fullName,
                language = blog.language ?: "en",
                facebookUrl = blog.facebookId?.let { "https://www.facebook.com/$it" },
                linkedInUrl = blog.linkedinId?.let { "https://www.linkedin.com/in/$it" },
                twitterUrl = blog.twitterId?.let { "https://www.twitter.com/$it" },
                youtubeUrl = blog.youtubeId?.let { "https://www.youtube.com/$it" },
                githubUrl = blog.githubId?.let { "https://www.github.com/$it" },
                whatsappUrl = blog.whatsappId?.let { "https://wa.me/" + formatWhatsAppNumber(it) },
                subscribedUrl = "$webappUrl/@/${blog.name}",
                unsubscribedUrl = recipient?.let { getUnsubscribeUrl(blog, recipient) },
            ),
        )
    }

    private fun formatWhatsAppNumber(number: String): String {
        val tmp = number.trim()
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "")
        return if (tmp.startsWith("+")) tmp.substring(1) else tmp
    }

    protected fun getLanguage(recipient: UserEntity): String =
        recipient.language ?: "en"

    protected fun formatMoney(amount: Long, wallet: WalletEntity): String {
        val country = Country.all.find { it.code.equals(wallet.country, true) }
        val fmt = country?.let { DecimalFormat(country.monetaryFormat) }

        return fmt?.let { fmt.format(amount) } ?: "$amount ${country?.currencySymbol}"
    }
}
