package com.wutsi.blog.config

import com.wutsi.blog.mail.service.MailFilterSet
import com.wutsi.blog.mail.service.filter.ButtonFilter
import com.wutsi.blog.mail.service.filter.CSSFilter
import com.wutsi.blog.mail.service.filter.DecoratorFilter
import com.wutsi.blog.mail.service.filter.ImageFilter
import com.wutsi.blog.mail.service.filter.LinkFilter
import com.wutsi.blog.mail.service.filter.VideoFilter
import com.wutsi.blog.subscription.service.EmailValidatorSet
import com.wutsi.blog.subscription.service.validator.EmailDomainValidator
import com.wutsi.blog.subscription.service.validator.EmailFormatValidator
import com.wutsi.blog.subscription.service.validator.EmailRoleValidator
import com.wutsi.platform.core.image.ImageService
import org.apache.commons.io.IOUtils
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
    private val imageService: ImageService,
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.application.website-url}") private val websiteUrl: String,
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
            ImageFilter(imageService),
            VideoFilter(assetUrl),
            ButtonFilter(),

            CSSFilter(), // Should be before last
            LinkFilter("$websiteUrl/wclick"), // Should be last
        ),
    )

    @Bean
    fun emailValidatorSet() = EmailValidatorSet(
        listOf(
            EmailFormatValidator(),
            EmailDomainValidator(loadStringList("/email/domain.txt")),
            EmailRoleValidator(loadStringList("/email/role.txt"))
        )
    )

    private fun loadStringList(path: String): List<String> {
        val text = IOUtils.toString(MailConfiguration::class.java.getResourceAsStream(path))
        return text.split("[\r\n]+".toRegex())
    }

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
