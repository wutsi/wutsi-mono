package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.domain.StoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StoryContentRepository : CrudRepository<StoryContentEntity, Long> {
    fun findByStory(story: StoryEntity): List<StoryContentEntity>
    fun findByStoryAndLanguage(story: StoryEntity, language: String?): Optional<StoryContentEntity>
}
