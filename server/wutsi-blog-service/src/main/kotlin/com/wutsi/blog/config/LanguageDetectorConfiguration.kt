package com.wutsi.blog.config

import org.apache.tika.language.detect.LanguageDetector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LanguageDetectorConfiguration {
    @Bean
    fun getLanguageDetector(): LanguageDetector =
        LanguageDetector.getDefaultLanguageDetector().loadModels()
}
