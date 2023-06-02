package com.wutsi.blog.app.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class LocalizationService(
    private val messages: MessageSource,
) {
    fun getLocale() = LocaleContextHolder.getLocale()

    fun getMessage(key: String, args: Array<Any>? = null, locale: Locale? = null): String {
        val loc = if (locale == null) getLocale() else locale
        return messages.getMessage(key, args, loc)
    }
}
