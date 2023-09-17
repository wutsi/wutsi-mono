package com.wutsi.blog.story.service

import com.wutsi.blog.story.dto.SearchSimilarStoryRequest

interface StorySimilarityStrategy {
    fun search(request: SearchSimilarStoryRequest): List<Long>
}
