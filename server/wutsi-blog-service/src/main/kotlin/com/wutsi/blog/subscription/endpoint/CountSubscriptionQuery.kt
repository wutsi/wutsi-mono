package com.wutsi.blog.like.endpoint

import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.dto.CountLikeRequest
import com.wutsi.blog.like.dto.CountLikeResponse
import com.wutsi.blog.like.dto.LikeCounter
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/likes/queries/count")
class CountLikeQuery(
    private val storyDao: LikeStoryRepository,
    private val likeDao: LikeRepository,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: CountLikeRequest,
    ): CountLikeResponse {
        // Stories
        val stories = storyDao.findAllById(request.storyIds.toSet()).toList()
        if (stories.isEmpty()) {
            return CountLikeResponse()
        }

        // Liked stories
        val liked: List<Long> = if (request.userId != null) {
            likeDao.findByStoryIdInAndUserId(stories.map { it.storyId }, request.userId!!).map { it.storyId }
        } else if (request.deviceId != null) {
            likeDao.findByStoryIdInAndDeviceId(stories.map { it.storyId }, request.deviceId!!).map { it.storyId }
        } else {
            emptyList()
        }

        // Result
        return CountLikeResponse(
            counters = stories.map {
                LikeCounter(
                    storyId = it.storyId,
                    count = it.count,
                    liked = liked.contains(it.storyId),
                )
            },
        )
    }
}
