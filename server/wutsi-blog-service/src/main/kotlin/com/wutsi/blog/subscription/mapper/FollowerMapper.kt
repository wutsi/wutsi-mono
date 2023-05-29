package com.wutsi.blog.subscription.mapper

import com.wutsi.blog.client.follower.FollowerCountDto
import com.wutsi.blog.client.follower.FollowerDto
import com.wutsi.blog.subscription.domain.Follower
import com.wutsi.blog.subscription.domain.FollowerCount
import org.springframework.stereotype.Service

@Deprecated("")
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
