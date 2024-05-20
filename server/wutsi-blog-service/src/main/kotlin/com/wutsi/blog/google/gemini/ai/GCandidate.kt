package com.wutsi.blog.google.gemini.ai

data class GCandidate(
    val content: GContent = GContent(),
    val index: Int = 0,
    val finishedReason: String = "",
    val safetyRatings: List<GSafetyRating> = emptyList(),
)