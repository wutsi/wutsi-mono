package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.TagBackend
import com.wutsi.blog.app.model.TagModel
import com.wutsi.blog.app.mapper.TagMapper
import org.springframework.stereotype.Service

@Service
class TagService(
    private val api: TagBackend,
    private val mapper: TagMapper,
) {
    fun search(query: String): List<TagModel> {
        val tags = api.search(query).tags
        return tags.map { mapper.toTagModel(it) }
    }
}
