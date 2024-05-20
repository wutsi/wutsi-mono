package com.wutsi.blog.google.gemini.ai

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class Gemini(
    @Value("\${wutsi.application.google.gemini.api-key}") private val apiKey: String,
    @Value("\${wutsi.application.google.gemini.model}") private val model: String,
    private val rest: RestTemplate,
) {
    fun generateContent(prompts: List<String>): GenerateContentResponse {
        val response = rest.postForEntity(
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey",
            GenerateContentRequest(
                contents = listOf(
                    GContent(
                        parts = prompts.map { prompt -> GPart(text = prompt) }
                    )
                )
            ),
            GenerateContentResponse::class.java
        )
        return response.body!!
    }
}