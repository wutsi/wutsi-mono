package com.wutsi.blog.story.mapper

import com.wutsi.blog.story.domain.TagEntity
import com.wutsi.blog.story.dto.Tag
import org.springframework.stereotype.Service

@Service
class TagMapper {
    fun toTagDto(tag: TagEntity) = Tag(
        id = tag.id!!,
        name = tag.name,
        displayName = tag.displayName,
        totalStories = tag.totalStories,
    )
}
