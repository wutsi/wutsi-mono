package com.wutsi.blog.like.endpoint

import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.dto.Like
import com.wutsi.blog.like.dto.SearchLikeRequest
import com.wutsi.blog.like.dto.SearchLikeResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/likes/queries/search")
class SearchLikeQuery(
    private val storyDao: LikeStoryRepository,
    private val likeDao: LikeRepository,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: SearchLikeRequest,
    ): SearchLikeResponse {
        // Stories
        val stories = storyDao.findAllById(request.storyIds.toSet()).toList()
        if (stories.isEmpty()) {
            return SearchLikeResponse()
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
        return SearchLikeResponse(
            likes = stories.map {
                Like(
                    storyId = it.storyId,
                    count = it.count,
                    liked = liked.contains(it.storyId),
                )
            },
        )
    }
}
