package com.wutsi.blog.google.gemini.ai

interface Gemini {
    fun generateContent(prompts: List<String>): GenerateContentResponse
}