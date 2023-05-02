package com.wutsi.blog.story.service.sort

import com.wutsi.blog.client.story.SortStoryRequest

interface SortAlgorithm {
    fun sort(request: SortStoryRequest): List<Long>
}
