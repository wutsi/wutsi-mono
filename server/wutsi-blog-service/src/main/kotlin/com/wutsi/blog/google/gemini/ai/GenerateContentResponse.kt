package com.wutsi.blog.google.gemini.ai

data class GenerateContentResponse(
    val candidates: List<GCandidate> = emptyList(),
    val promptFeedback: GPromptFeedback = GPromptFeedback(),
)