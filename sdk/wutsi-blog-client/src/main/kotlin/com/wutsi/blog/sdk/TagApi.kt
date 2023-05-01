package com.wutsi.blog.sdk

import com.wutsi.blog.client.story.SearchTagResponse

interface TagApi {
    fun search(query: String): SearchTagResponse
}
