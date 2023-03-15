package com.wutsi.platform.core.util.feign

import feign.RequestTemplate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import java.util.Locale

internal class FeignAcceptLanguageInterceptorTest {
    private lateinit var interceptor: FeignAcceptLanguageInterceptor
    private lateinit var template: RequestTemplate

    @BeforeEach
    fun setUp() {
        interceptor = FeignAcceptLanguageInterceptor()
        template = RequestTemplate()
    }

    @Test
    fun header() {
        LocaleContextHolder.setLocale(Locale.FRANCE)

        interceptor.apply(template)

        assertEquals(true, template.headers()[HttpHeaders.ACCEPT_LANGUAGE]?.contains("fr"))
    }
}
