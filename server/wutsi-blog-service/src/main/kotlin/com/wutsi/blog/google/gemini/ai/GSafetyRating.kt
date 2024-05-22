package com.wutsi.blog.google.gemini.ai

data class GSafetyRating(
    val category: GHarmCategory,
    val probability: GHarmProbability,
)