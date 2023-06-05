package com.wutsi.blog.story.migration

import com.wutsi.blog.story.domain.Story

interface StoryMigrator {
    fun migrate(item: Story)
}
