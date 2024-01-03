package com.wutsi.blog.app.util

import java.net.URLEncoder

object WhatsappUtil {
    fun url(phoneNumber: String, text: String? = null, url: String? = null): String {
        val query = if (text.isNullOrEmpty()) {
            ""
        } else {
            "?text=" + URLEncoder.encode(toWhatsAppText(text, url), Charsets.UTF_8)
        }
        val phone = sanitize(phoneNumber)
        return "https://wa.me/$phone$query"
    }

    private fun toWhatsAppText(text: String? = null, url: String? = null): String {
        val buff = StringBuilder()
        text?.let { buff.append(text) }
        url?.let {
            if (buff.isNotEmpty()) {
                buff.append("\n")
            }
            buff.append(url)
        }
        return buff.toString()
    }

    private fun sanitize(phoneNumber: String): String {
        val tmp = phoneNumber.trim()
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "")
        return if (tmp.startsWith("+")) tmp.substring(1) else tmp
    }
}
