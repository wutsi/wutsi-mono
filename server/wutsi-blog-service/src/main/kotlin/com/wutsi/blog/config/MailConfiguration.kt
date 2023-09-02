package com.wutsi.blog.config

import com.wutsi.blog.mail.service.MailFilterSet
import com.wutsi.blog.mail.service.filter.ButtonFilter
import com.wutsi.blog.mail.service.filter.CSSFilter
import com.wutsi.blog.mail.service.filter.DecoratorFilter
import com.wutsi.blog.mail.service.filter.ImageFilter
import com.wutsi.blog.mail.service.filter.UTMFilter
import com.wutsi.blog.mail.service.filter.VideoFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class MailConfiguration(
    private val messageSource: MessageSource,
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
) {
    @Bean
    fun emailTemplateEngine(): TemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.addTemplateResolver(htmlTemplateResolver())
        templateEngine.setTemplateEngineMessageSource(messageSource)
        return templateEngine
    }

    @Bean
    fun mailFilterSet() = MailFilterSet(
        listOf(
            DecoratorFilter(messageSource),
            ImageFilter(),
            UTMFilter(),
            VideoFilter(assetUrl),
            ButtonFilter(),
            CSSFilter(), // Should be last
        ),
    )

    private fun htmlTemplateResolver(): ITemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.order = Integer.valueOf(2)
        templateResolver.prefix = "/templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = Charsets.UTF_8.name()
        templateResolver.isCacheable = false
        return templateResolver
    }
}
