package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.domain.StoryContent
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StoryContentRepository : CrudRepository<StoryContent, Long> {
    fun findByStory(story: Story): List<StoryContent>
    fun findByStoryAndLanguage(story: Story, language: String?): Optional<StoryContent>
}
