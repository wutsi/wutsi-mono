package com.wutsi.enums.util

import com.wutsi.enums.ChannelType
import java.net.URL
import java.net.URLDecoder
import java.util.Locale

open class ChannelDetector {
    companion object {
        private val SEO_DOMAINS = arrayListOf(
            "google",
            "bing",
            "yahoo",
        )
        private val EMAIL_DOMAINS = arrayListOf(
            "mail.google.com",
            "mail.yahoo.com",
            "outlook.live.com",
        )
        private val SOCIAL_DOMAINS = arrayListOf(
            "facebook.com",
            "t.co",
            "twitter.com",
            "linkedin.com",
            "pinterest.com",
            "instagram.com",
            "snapchat.com",
        )
        private val MESSAGING_DOMAINS = arrayListOf(
            // Whatsapp - https://www.netify.ai/resources/applications/whatsapp
            "wa.me",
            "whatsapp.com",

            // Telegram - see https://www.netify.ai/resources/applications/telegram
            "t.me",
            "telegram.me",
            "telegram.org",
            "telegram.com",

            // Messenger - see https://www.netify.ai/resources/applications/messenger
            "m.me",
            "messenger.com",
            "msngr.com",
        )
    }

    open fun detect(url: String, referer: String, ua: String): ChannelType {
        // APP
        if (ua.contains("(dart:io)", true)) {
            return ChannelType.APP
        }

        // Query string
        val medium = extractParams(url)["utm_medium"]
        if (medium != null) {
            try {
                return ChannelType.valueOf(medium.uppercase())
            } catch (ex: Exception) {
                // Ignore
            }
        }

        // User Agent
        if (
            ua.contains("WhatsApp") ||
            ua.contains("FBAN/Messenger") ||
            ua.contains("FB_IAB/MESSENGER") ||
            ua.contains("TelegramBot (like TwitterBot)")
        ) {
            return ChannelType.MESSAGING
        } else if (
            ua.contains("Twitter") ||
            ua.contains("TikTok") ||
            ua.contains("FBAN/FB")
        ) {
            return ChannelType.SOCIAL
        }

        // Referer
        val domain = extractDomainName(referer)
        return if (EMAIL_DOMAINS.contains(domain)) {
            ChannelType.EMAIL
        } else if (SEO_DOMAINS.find { domain.contains(it) } != null) {
            ChannelType.SEO
        } else if (SOCIAL_DOMAINS.find { domain.contains(it) } != null) {
            ChannelType.SOCIAL
        } else if (MESSAGING_DOMAINS.contains(domain)) {
            ChannelType.MESSAGING
        } else {
            ChannelType.WEB
        }
    }

    private fun extractDomainName(url: String?): String {
        if (url == null) {
            return ""
        }

        var domainName = url

        var index = domainName.indexOf("://")

        if (index != -1) {
            // keep everything after the "://"
            domainName = domainName.substring(index + 3)
        }

        index = domainName.indexOf('/')

        if (index != -1) {
            // keep everything before the '/'
            domainName = domainName.substring(0, index)
        }

        // check for and remove a preceding 'www'
        // followed by any sequence of characters (non-greedy)
        // followed by a '.'
        // from the beginning of the string
        domainName = domainName.replaceFirst("^www.*?\\.".toRegex(), "")

        return domainName.lowercase(Locale.getDefault())
    }

    private fun extractParams(url: String): Map<String, String?> {
        try {
            val params = LinkedHashMap<String, String>()
            val query = URL(url).query
            val pairs = query.split("&".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

            for (pair in pairs) {
                val idx = pair.indexOf("=")
                val name = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                val value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                params[name] = value
            }
            return params
        } catch (ex: Exception) {
            return emptyMap()
        }
    }
}
