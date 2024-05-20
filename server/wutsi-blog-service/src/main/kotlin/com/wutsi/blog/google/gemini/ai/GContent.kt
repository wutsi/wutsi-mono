package com.wutsi.blog.google.gemini.ai

data class GContent(
    val parts: List<GPart> = emptyList(),
    val role: String = "",
)