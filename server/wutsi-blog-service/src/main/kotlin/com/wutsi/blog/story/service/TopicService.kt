package com.wutsi.blog.story.service

import com.wutsi.blog.story.dao.TopicRepository
import com.wutsi.blog.story.domain.Topic
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class TopicService(
    private val dao: TopicRepository,
) {
    fun all(): List<Topic> =
        dao.findAll().toList()

    fun findById(id: Long) = dao.findById(id)
        .orElseThrow { NotFoundException(Error("topic_not_found")) }
}
