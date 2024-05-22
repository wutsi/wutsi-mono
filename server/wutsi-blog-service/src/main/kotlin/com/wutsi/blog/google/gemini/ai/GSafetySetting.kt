package com.wutsi.blog.google.gemini.ai

data class GSafetySetting(
    val category: GHarmCategory,
    val threshold: GHarmBlockThreshold = GHarmBlockThreshold.BLOCK_NONE,
)