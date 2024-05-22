package com.wutsi.blog.google.gemini.ai

data class GPromptFeedback(
    val safetyRatings: List<GSafetyRating> = emptyList(),
    val blockReason: String = "",
)