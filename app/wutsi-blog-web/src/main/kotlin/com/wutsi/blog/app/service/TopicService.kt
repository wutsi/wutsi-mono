package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.TopicBackend
import com.wutsi.blog.app.mapper.TopicMapper
import com.wutsi.blog.app.model.TopicModel
import org.springframework.stereotype.Service

@Service
class TopicService(
    private val api: TopicBackend,
    private val mapper: TopicMapper,
) {
    private var topics: List<TopicModel>? = null

    fun all(): List<TopicModel> {
        if (topics == null) {
            topics = api.all().topics.map { mapper.toTopicMmodel(it) }
        }
        return topics!!
    }

    fun get(id: Long) = all().find { it.id == id }
}
