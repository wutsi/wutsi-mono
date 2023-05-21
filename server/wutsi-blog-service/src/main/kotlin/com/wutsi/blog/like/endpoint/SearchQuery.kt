package com.wutsi.blog.like.endpoint

import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.dto.Like
import com.wutsi.blog.like.dto.SearchLikeResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/likes/queries/search")
class SearchQuery(
    private val storyDao: LikeStoryRepository,
    private val likeDao: LikeRepository,
) {
    @GetMapping
    fun search(
        @RequestParam(name = "story-id") storyIds: Array<Long>,
        @RequestParam(name = "user-id", required = false) userId: Long? = null,
        @RequestParam(name = "device-id", required = false) deviceId: String? = null,
    ): SearchLikeResponse {
        // Stories
        val stories = storyDao.findAllById(storyIds.toSet()).toList()
        if (stories.isEmpty()) {
            return SearchLikeResponse()
        }

        // Liked stories
        val liked: List<Long> = if (userId != null) {
            likeDao.findByStoryIdInAndUserId(stories.map { it.storyId }, userId).map { it.storyId }
        } else if (deviceId != null) {
            likeDao.findByStoryIdInAndDeviceId(stories.map { it.storyId }, deviceId).map { it.storyId }
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
