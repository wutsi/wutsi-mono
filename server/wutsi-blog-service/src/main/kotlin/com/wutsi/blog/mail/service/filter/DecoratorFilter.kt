package com.wutsi.blog.mail.service.filter

import com.github.mustachejava.DefaultMustacheFactory
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.springframework.context.MessageSource
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.Locale

class DecoratorFilter(
    private val messages: MessageSource,
) : MailFilter {
    override fun filter(body: String, context: MailContext): String {
        val template = "/templates/mail/decorator/${context.template}.html"
        val reader = InputStreamReader(DecoratorFilter::class.java.getResourceAsStream(template))
        val scope = scope(body, context)
        reader.use {
            val writer = StringWriter()
            writer.use {
                val mustache = DefaultMustacheFactory().compile(reader, "text")
                mustache.execute(
                    writer,
                    mapOf(
                        "scope" to scope,
                    ),
                )
                writer.flush()
                return writer.toString()
            }
        }
    }

    private fun scope(body: String, context: MailContext) = mapOf(
        "siteUrl" to "${context.websiteUrl}/@/${context.blog.name}",
        "assetUrl" to context.assetUrl,
        "logoUrl" to context.blog.logoUrl,
        "siteName" to context.blog.fullName,
        "siteHandle" to "@${context.blog.fullName}",
        "facebookUrl" to context.blog.facebookUrl?.ifEmpty { null },
        "youtubeUrl" to context.blog.youtubeUrl?.ifEmpty { null },
        "linkedInUrl" to context.blog.linkedInUrl?.ifEmpty { null },
        "twitterUrl" to context.blog.twitterUrl?.ifEmpty { null },
        "githubUrl" to context.blog.githubUrl?.ifEmpty { null },
        "whatsappUrl" to context.blog.whatsappUrl,
        "subscribeUrl" to context.blog.subscribedUrl,
        "unsubscribeUrl" to context.blog.unsubscribedUrl,
        "subscribeText" to getMessage("button.subscribe", context.blog.language),
        "unsubscribeText" to getMessage("button.unsubscribe", context.blog.language),
        "body" to body,
    )

    private fun getMessage(key: String, language: String) =
        try {
            messages.getMessage(key, emptyArray(), Locale(language))
        } catch (ex: Exception) {
            key
        }
}
