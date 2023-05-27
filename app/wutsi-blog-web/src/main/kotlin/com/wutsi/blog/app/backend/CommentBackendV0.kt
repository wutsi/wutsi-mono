package com.wutsi.blog.app.backend

import com.wutsi.blog.client.comment.CountCommentResponse
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.CreateCommentResponse
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.comment.SearchCommentResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat

@Service
class CommentBackend(private val http: RestTemplate) {
    @Value("\${wutsi.application.backend.comment.endpoint}")
    private lateinit var endpoint: String

    fun create(request: CreateCommentRequest): CreateCommentResponse =
        http.postForEntity(uri(), request, CreateCommentResponse::class.java).body!!

    fun search(request: SearchCommentRequest): SearchCommentResponse {
        val url = uri() + "?" + params(request, true)
        return http.getForEntity(url, SearchCommentResponse::class.java).body!!
    }

    fun count(request: SearchCommentRequest): CountCommentResponse {
        val url = uri("/count") + "?" + params(request, false)
        return http.getForEntity(url, CountCommentResponse::class.java).body!!
    }

    fun delete(id: Long) =
        http.delete(uri("/$id"))

    private fun params(request: SearchCommentRequest, includeLimitOffset: Boolean): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val buff = mutableListOf<String>()
        request.authorId?.let { buff.add("authorId=$it") }
        request.since?.let { buff.add("since=" + fmt.format(it)) }

        request.storyIds.forEach {
            buff.add("storyId=$it")
        }

        if (includeLimitOffset) {
            buff.add("limit=${request.limit}")
            buff.add("offset=${request.offset}")
        }

        return buff.joinToString(separator = "&")
    }

    private fun uri(path: String = ""): String =
        "$endpoint$path"
}
