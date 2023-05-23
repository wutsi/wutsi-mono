package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.model.TopicModel
import com.wutsi.blog.client.story.TopicDto
import org.springframework.stereotype.Service

@Service
class TopicMapper(private val requestContext: RequestContext) {
    fun toTopicMmodel(topic: TopicDto) = TopicModel(
        id = topic.id,
        parentId = topic.parentId,
        name = topic.name,
        displayName = toDisplayName(topic.name),
    )

    private fun toDisplayName(name: String): String {
        try {
            return requestContext.getMessage("topic.$name")
        } catch (ex: Exception) {
            return name
        }
    }
}
