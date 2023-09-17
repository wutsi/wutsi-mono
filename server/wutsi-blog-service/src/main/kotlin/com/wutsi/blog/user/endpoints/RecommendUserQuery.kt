package com.wutsi.blog.user.endpoints

import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.dto.RecommendUserResponse
import com.wutsi.blog.user.service.UserRecommendationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class RecommendUserQuery(
    private val service: UserRecommendationService,
) {
    @PostMapping("/v1/users/queries/recommend")
    fun recommend(@Valid @RequestBody request: RecommendUserRequest): RecommendUserResponse =
        RecommendUserResponse(
            userIds = service.recommend(request),
        )
}
