package com.wutsi.blog.story.mapper

import com.wutsi.blog.client.story.TagDto
import com.wutsi.blog.story.domain.Tag
import org.springframework.stereotype.Service

@Service
class TagMapper {
    fun toTagDto(tag: Tag) = TagDto(
        id = tag.id!!,
        name = tag.name,
        displayName = tag.displayName,
        totalStories = tag.totalStories,
    )
}
