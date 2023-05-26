package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.TagModel
import com.wutsi.blog.client.story.TagDto
import org.springframework.stereotype.Service

@Service
class TagMapper {
    fun toTagModel(tag: TagDto) = TagModel(
        id = tag.id,
        name = tag.name,
        displayName = tag.displayName,
        totalStories = tag.totalStories,
    )
}
