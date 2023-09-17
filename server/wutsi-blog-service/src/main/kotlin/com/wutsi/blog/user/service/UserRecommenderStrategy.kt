package com.wutsi.blog.user.service

import com.wutsi.blog.user.dto.RecommendUserRequest

interface UserRecommenderStrategy {
    fun recommend(request: RecommendUserRequest): List<Long>
}
