package com.wutsi.blog.google.gemini.ai.v1beta

import com.wutsi.blog.google.gemini.ai.GContent
import com.wutsi.blog.google.gemini.ai.GPart
import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.google.gemini.ai.GenerateContentRequest
import com.wutsi.blog.google.gemini.ai.GenerateContentResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestTemplate

class GeminiV1Beta(
    @Value("\${wutsi.application.google.gemini.api-key}") private val apiKey: String,
    @Value("\${wutsi.application.google.gemini.model}") private val model: String,
) : Gemini {
    private val rest = RestTemplate()

    override fun generateContent(prompts: List<String>): GenerateContentResponse {
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