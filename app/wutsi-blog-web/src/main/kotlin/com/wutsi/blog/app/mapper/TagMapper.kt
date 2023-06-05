package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.TagModel
import com.wutsi.blog.story.dto.Tag
import org.springframework.stereotype.Service

@Service
class TagMapper {
    fun toTagModel(tag: Tag) = TagModel(
        id = tag.id,
        name = tag.name,
        displayName = tag.displayName,
        totalStories = tag.totalStories,
    )
}
