package com.wutsi.blog.mail.service.sender

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.model.BlogModel
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.image.ImageService
import org.springframework.beans.factory.annotation.Autowired
import java.text.DecimalFormat

abstract class AbstractBlogMailSender : AbstractMailSender() {
    companion object {
        const val TEMPLATE = "default"
    }

    @Autowired
    protected lateinit var imageService: ImageService

    protected open fun getUnsubscribeUrl(blog: UserEntity, recipient: UserEntity): String? =
        "$webappUrl/@/${blog.name}/unsubscribe?email=${recipient.email}"

    protected fun createMailContext(blog: UserEntity, recipient: UserEntity?, story: StoryEntity? = null): MailContext {
        return MailContext(
            storyId = story?.id,
            assetUrl = assetUrl,
            websiteUrl = webappUrl,
            template = TEMPLATE,
            blog = BlogModel(
                name = blog.name,
                logoUrl = blog.pictureUrl?.let { url -> imageService.transform(url) },
                fullName = blog.fullName,
                language = blog.language ?: "en",
                facebookUrl = toFacebookUrl(blog),
                linkedInUrl = blog.linkedinId?.let { "https://www.linkedin.com/in/$it" },
                twitterUrl = blog.twitterId?.let { "https://www.twitter.com/$it" },
                youtubeUrl = blog.youtubeId?.let { "https://www.youtube.com/$it" },
                githubUrl = blog.githubId?.let { "https://www.github.com/$it" },
                whatsappUrl = toWhatsappUrl(blog),
                subscribedUrl = "$webappUrl/@/${blog.name}",
                unsubscribedUrl = recipient?.let { getUnsubscribeUrl(blog, recipient) },
                biography = blog.biography?.ifEmpty { null }
            ),
        )
    }

    protected fun toWhatsappUrl(blog: UserEntity): String? =
        blog.whatsappId?.let { "https://wa.me/" + formatWhatsAppNumber(it) }

    protected fun toFacebookUrl(blog: UserEntity): String? =
        blog.facebookId?.let { "https://www.facebook.com/$it" }

    private fun formatWhatsAppNumber(number: String): String {
        val tmp = number.trim()
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "")
        return if (tmp.startsWith("+")) tmp.substring(1) else tmp
    }

    protected fun formatMoney(amount: Long, wallet: WalletEntity): String {
        val country = Country.all.find { it.code.equals(wallet.country, true) }
        val fmt = country?.let { DecimalFormat(country.monetaryFormat) }

        return fmt?.let { fmt.format(amount) } ?: "$amount ${country?.currencySymbol}"
    }
}
