package com.wutsi.blog.google.gemini.ai.v1

import com.wutsi.blog.google.gemini.ai.GContent
import com.wutsi.blog.google.gemini.ai.GHarmBlockThreshold
import com.wutsi.blog.google.gemini.ai.GHarmCategory
import com.wutsi.blog.google.gemini.ai.GPart
import com.wutsi.blog.google.gemini.ai.GSafetySetting
import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.google.gemini.ai.GenerateContentRequest
import com.wutsi.blog.google.gemini.ai.GenerateContentResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestTemplate

class GeminiV1(
    @Value("\${wutsi.application.google.gemini.api-key}") private val apiKey: String,
    @Value("\${wutsi.application.google.gemini.model}") private val model: String,
) : Gemini {
    private val rest = RestTemplate()

    override fun generateContent(prompts: List<String>): GenerateContentResponse {
        val request = GenerateContentRequest(
            contents = listOf(
                GContent(
                    parts = prompts.map { prompt -> GPart(text = prompt) }
                )
            ),
            safetySettings = listOf(
                GSafetySetting(GHarmCategory.HARM_CATEGORY_HARASSMENT, GHarmBlockThreshold.BLOCK_NONE),
                GSafetySetting(GHarmCategory.HARM_CATEGORY_HATE_SPEECH, GHarmBlockThreshold.BLOCK_NONE),
                GSafetySetting(GHarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT, GHarmBlockThreshold.BLOCK_NONE),
                GSafetySetting(GHarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT, GHarmBlockThreshold.BLOCK_NONE),
            )
        )
        val response = rest.postForEntity(
            "https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey",
            request,
            GenerateContentResponse::class.java
        )
        return response.body!!
    }
}