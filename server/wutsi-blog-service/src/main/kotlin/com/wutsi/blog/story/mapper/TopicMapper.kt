package com.wutsi.blog.story.mapper

import com.wutsi.blog.client.story.TopicDto
import com.wutsi.blog.story.domain.Topic
import org.springframework.stereotype.Service

@Service
class TopicMapper {
    fun toTopicDto(topic: Topic) = TopicDto(
        id = topic.id!!,
        parentId = topic.parentId?.let { it } ?: -1,
        name = topic.name,
    )
}
