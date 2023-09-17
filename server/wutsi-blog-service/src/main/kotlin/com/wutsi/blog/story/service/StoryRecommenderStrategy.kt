package com.wutsi.blog.story.service

import com.wutsi.blog.story.dto.RecommendStoryRequest

interface StoryRecommenderStrategy {
    fun recommend(request: RecommendStoryRequest): List<Long>
}
