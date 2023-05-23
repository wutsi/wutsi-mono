package com.wutsi.blog.like.mapper

import com.wutsi.blog.client.like.LikeCountDto
import com.wutsi.blog.client.like.LikeDto
import com.wutsi.blog.like.domain.LikeCount
import com.wutsi.blog.like.domain.LikeV0
import org.springframework.stereotype.Service

@Deprecated("")
@Service
class LikeMapper {
    fun toLikeDto(obj: LikeV0) = LikeDto(
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
