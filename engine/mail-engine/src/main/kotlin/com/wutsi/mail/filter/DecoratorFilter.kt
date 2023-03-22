package com.wutsi.mail.filter

import com.github.mustachejava.DefaultMustacheFactory
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.mail.MailContext
import com.wutsi.mail.MailFilter
import java.io.InputStreamReader
import java.io.StringWriter

class DecoratorFilter : MailFilter {
    override fun filter(body: String, context: MailContext): String {
        val template = context.template?.let { "/decorators/$it.html" } ?: "/decorators/default.html"
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
        "siteUrl" to context.merchant.url,
        "logoUrl" to context.merchant.logoUrl,
        "siteName" to context.merchant.name,
        "location" to context.merchant.location,
        "category" to context.merchant.category,
        "assetUrl" to context.assetUrl,
        "phoneNumber" to formatPhone(context.merchant.phoneNumber, context.merchant.country),
        "facebookUrl" to context.merchant.facebookId?.let { "https://www.facebook.com/$it" },
        "instagramUrl" to context.merchant.instagramId?.let { "https://www.instagram.com/$it" },
        "twitterUrl" to context.merchant.twitterId?.let { "https://www.twitter.com/$it" },
        "youtubeUrl" to context.merchant.youtubeId?.let { "https://www.youtube.com/$it" },
        "websiteUrl" to context.merchant.websiteUrl,
        "phoneUrl" to "tel: ${context.merchant.phoneNumber}",
        "whatsappUrl" to if (context.merchant.whatsapp) {
            "https://wa.me/" + context.merchant.phoneNumber.substring(1)
        } else {
            null
        },
        "body" to body,
    )

    private fun formatPhone(phoneNumber: String, country: String): String? {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val number = phoneNumberUtil.parse(phoneNumber, country)
        return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }
}
