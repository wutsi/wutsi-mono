package com.wutsi.blog.app.backend

import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.story.dto.SearchReaderResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ReaderBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.reader.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchReaderRequest): SearchReaderResponse {
        return rest.postForEntity("$endpoint/queries/search", request, SearchReaderResponse::class.java).body!!
    }
}
