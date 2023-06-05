package com.wutsi.blog.client.story

import com.wutsi.blog.story.dto.Story

@Deprecated("")
data class TranslateStoryResponse(
    val story: Story = Story(),
)
