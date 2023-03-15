package com.wutsi.platform.core.util.feign

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders

class FeignAcceptLanguageInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        val locale = LocaleContextHolder.getLocale()
        if (locale != null) {
            template.header(HttpHeaders.ACCEPT_LANGUAGE, locale.language)
        }
    }
}
