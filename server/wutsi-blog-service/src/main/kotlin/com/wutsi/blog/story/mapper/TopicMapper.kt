package com.wutsi.blog.story.mapper

import com.wutsi.blog.story.domain.TopicEntity
import com.wutsi.blog.story.dto.Topic
import org.springframework.stereotype.Service

@Service
class TopicMapper {
    fun toTopicDto(topic: TopicEntity) = Topic(
        id = topic.id!!,
        parentId = topic.parentId?.let { it } ?: -1,
        name = topic.name,
    )
}
