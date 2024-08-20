package com.wutsi.blog.app.config

import com.wutsi.blog.app.service.LocaleResolverImpl
import com.wutsi.blog.app.service.RequestContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor

@Configuration
class WebConfiguration : WebMvcConfigurer {
    @Value("\${wutsi.application.bot.language}")
    private lateinit var botLanguage: String

    @Bean
    fun localeResolver(requestContext: RequestContext): LocaleResolver =
        LocaleResolverImpl(requestContext, botLanguage)

    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val lci = LocaleChangeInterceptor()
        lci.paramName = "lang"
        return lci
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)

        registry.addInterceptor(localeChangeInterceptor())
    }
}
