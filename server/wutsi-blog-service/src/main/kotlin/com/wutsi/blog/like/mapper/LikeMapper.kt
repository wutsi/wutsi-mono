package com.wutsi.blog.like.mapper

import com.wutsi.blog.client.like.LikeCountDto
import com.wutsi.blog.client.like.LikeDto
import com.wutsi.blog.like.domain.Like
import com.wutsi.blog.like.domain.LikeCount
import org.springframework.stereotype.Service

@Service
class LikeMapper {
    fun toLikeDto(obj: Like) = LikeDto(
        id = obj.id?.let { it } ?: -1,
        userId = obj.user?.id,
        storyId = obj.story.id?.let { it } ?: -1,
        deviceId = obj.deviceId,
        likeDateTime = obj.likeDateTime,
    )

    fun toLikeCountDto(obj: LikeCount) = LikeCountDto(
        storyId = obj.storyId,
        value = obj.value,
    )
}
