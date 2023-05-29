package com.wutsi.blog.subscription

import com.wutsi.blog.client.event.FollowEvent
import com.wutsi.blog.client.event.UnfollowEvent
import com.wutsi.blog.client.follower.CountFollowerResponse
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerResponse
import com.wutsi.blog.subscription.mapper.FollowerMapper
import com.wutsi.blog.subscription.service.FollowerService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Deprecated("")
@RestController
@RequestMapping("/v1/followers")
class FollowerController(
    private val service: FollowerService,
    private val mapper: FollowerMapper,
    private val events: ApplicationEventPublisher,
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateFollowerRequest): CreateFollowerResponse {
        val follower = service.create(request)
        events.publishEvent(
            FollowEvent(
                userId = follower.userId,
                followerUserId = follower.followerUserId,
            ),
        )
        return CreateFollowerResponse(followerId = follower.id!!)
    }

    @GetMapping
    fun search(
        @RequestParam(required = false) userId: Long? = null,
        @RequestParam(required = false) followerUserId: Long? = null,
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

    @PostMapping("/search")
    fun search(@Valid @RequestBody request: SearchFollowerRequest): SearchFollowerResponse {
        val followers = service.search(request)
        return SearchFollowerResponse(
            followers = followers.map { mapper.toFollowerDto(it) },
        )
    }

    @GetMapping("/count")
    fun count(
        @RequestParam(required = false) userId: Long? = null,
        @RequestParam(required = false) followerUserId: Long? = null,
    ): CountFollowerResponse {
        val count = service.count(
            SearchFollowerRequest(
                userId = userId,
                followerUserId = followerUserId,
            ),
        )
        return CountFollowerResponse(
            counts = count.map { mapper.toFollowerCountDto(it) },
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        val follower = service.delete(id)
        events.publishEvent(
            UnfollowEvent(
                userId = follower.userId,
                followerUserId = follower.followerUserId,
            ),
        )
    }
}
