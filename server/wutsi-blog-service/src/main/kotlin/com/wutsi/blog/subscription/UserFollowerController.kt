package com.wutsi.blog.subscription

import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerResponse
import com.wutsi.blog.subscription.mapper.FollowerMapper
import com.wutsi.blog.subscription.service.FollowerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Deprecated("")
@RestController
class UserFollowerController(
    private val service: FollowerService,
    private val mapper: FollowerMapper,
) {

    @GetMapping("/v1/users/{user-id}/followers")
    fun userFollowers(
        @PathVariable(name = "user-id") userId: Long,
        @RequestParam(required = false, name = "follower-user-id") followerUserId: Long? = null,
        @RequestParam(required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(required = false, defaultValue = "0") offset: Int = 0,
    ): SearchFollowerResponse {
        val followers = service.search(
            SearchFollowerRequest(
                userId = userId,
                followerUserId = followerUserId,
                limit = limit,
                offset = offset,
            ),
        )
        return SearchFollowerResponse(
            followers = followers.map { mapper.toFollowerDto(it) },
        )
    }
}
