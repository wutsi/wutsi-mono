package com.wutsi.blog.story.service

import com.wutsi.blog.story.dto.ViewStoryCommand

interface ViewService {
    fun view(payload: ViewStoryCommand)
    fun getViews(userId: Long?): List<Long>
}
