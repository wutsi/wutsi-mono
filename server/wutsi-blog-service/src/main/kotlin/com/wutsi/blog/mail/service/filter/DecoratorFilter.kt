package com.wutsi.blog.mail.service.filter

import com.github.mustachejava.DefaultMustacheFactory
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import java.io.InputStreamReader
import java.io.StringWriter

class DecoratorFilter : MailFilter {
    override fun filter(body: String, context: MailContext): String {
        val template = "/mail/decorator/${context.template}.html"
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
        "body" to body,
    )
}
