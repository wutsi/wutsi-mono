package com.wutsi.blog.follower.mapper

import com.wutsi.blog.client.follower.FollowerCountDto
import com.wutsi.blog.client.follower.FollowerDto
import com.wutsi.blog.follower.domain.Follower
import com.wutsi.blog.follower.domain.FollowerCount
import org.springframework.stereotype.Service

@Service
class FollowerMapper {
    fun toFollowerDto(obj: Follower) = FollowerDto(
        id = obj.id?.let { it } ?: -1,
        userId = obj.userId,
        followerUserId = obj.followerUserId,
        followDateTime = obj.followDateTime,
    )

    fun toFollowerCountDto(obj: FollowerCount) = FollowerCountDto(
        userId = obj.userId,
        value = obj.value,
    )
}
