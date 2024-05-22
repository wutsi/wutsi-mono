package com.wutsi.blog.google.gemini.ai.configuration

import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.google.gemini.ai.v1.GeminiV1
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeminiConfiguration(
    @Value("\${wutsi.application.google.gemini.api-key}") private val apiKey: String,
    @Value("\${wutsi.application.google.gemini.model}") private val model: String,
) {
    @Bean
    fun gemini(): Gemini = GeminiV1(apiKey, model)
}