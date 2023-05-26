package com.wutsi.blog.pin.endpoint

import com.wutsi.blog.like.dto.Like
import com.wutsi.blog.like.dto.SearchLikeRequest
import com.wutsi.blog.like.dto.SearchLikeResponse
import com.wutsi.blog.pin.dao.PinStoryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/pins/queries/get")
class SearchQuery(
    private val storyDao: PinStoryRepository,
) {
    @GetMapping
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
