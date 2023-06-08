package com.wutsi.blog.app.backend

import com.wutsi.blog.story.dto.SearchTagResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder

@Service
class TagBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.tag.endpoint}")
    private lateinit var endpoint: String

    fun search(q: String): SearchTagResponse =
        rest.getForEntity(
            "$endpoint/queries/search?query=" + URLEncoder.encode(q, "utf-8"),
            SearchTagResponse::class.java,
        ).body!!
}
