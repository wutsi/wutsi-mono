package com.wutsi.blog.google.gemini.ai

data class GenerateContentRequest(
    val contents: List<GContent>,
    val safetySettings: List<GSafetySetting> = emptyList(),
)