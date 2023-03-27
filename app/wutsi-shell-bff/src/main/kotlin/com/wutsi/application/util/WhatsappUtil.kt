package com.wutsi.application.util

import java.net.URLEncoder

object WhatsappUtil {
    fun url(phoneNumber: String, text: String? = null, url: String? = null): String {
        val normalized = if (phoneNumber.startsWith("+")) {
            phoneNumber.substring(1)
        } else {
            phoneNumber
        }

        val query = if (text.isNullOrEmpty()) {
            ""
        } else {
            "?text=" + URLEncoder.encode(toWhatsAppText(text, url), Charsets.UTF_8)
        }
        return "https://wa.me/$normalized$query"
    }

    private fun toWhatsAppText(text: String? = null, url: String? = null): String {
        val buff = StringBuilder()
        text?.let { buff.append(text) }
        url?.let {
            if (buff.isNullOrEmpty()) {
                buff.append("\n")
            }
            buff.append(url)
        }
        return buff.toString()
    }

    fun sanitize(phoneNumber: String): String {
        val tmp = phoneNumber.trim()
        return if (tmp.startsWith("+")) {
            tmp
        } else {
            "+$tmp"
        }
    }
}
