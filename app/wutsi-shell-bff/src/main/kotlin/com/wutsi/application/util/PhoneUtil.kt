package com.wutsi.application.util

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.net.URLEncoder

object PhoneUtil {
    fun toWhatsAppUrl(phoneNumber: String?, text: String? = null, url: String? = null): String? {
        if (phoneNumber.isNullOrEmpty()) {
            return null
        }

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

    fun format(phoneNumber: String?, country: String? = null): String? {
        if (phoneNumber == null) {
            return null
        }

        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val number = phoneNumberUtil.parse(phoneNumber, country ?: "")
        return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }
}
