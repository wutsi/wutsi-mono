package com.wutsi.application.widget

import org.springframework.context.i18n.LocaleContextHolder
import java.text.MessageFormat
import java.util.Locale
import java.util.ResourceBundle

object WidgetL10n {
    private val defaultBundle = ResourceBundle.getBundle("widgets")
    private val frBundle = ResourceBundle.getBundle("widgets", Locale("fr"))

    fun getText(key: String, args: Array<Any> = emptyArray()): String =
        try {
            val locale = LocaleContextHolder.getLocale()
            val bundle = if (locale.language == "fr") {
                frBundle
            } else {
                defaultBundle
            }

            MessageFormat.format(bundle.getString(key), *args)
        } catch (ex: Exception) {
            key
        }
}
