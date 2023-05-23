package com.wutsi.blog.app.page.follower.service

import com.wutsi.blog.app.backend.FollowerBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerRequest
import org.springframework.stereotype.Service

@Service
class FollowerService(
    private val api: FollowerBackend,
    private val requestContext: RequestContext,
) {
    fun follow(userId: Long): Long = api.create(
        CreateFollowerRequest(
            userId = userId,
            followerUserId = requestContext.currentUser()?.id,
        ),
    ).followerId

    fun unfollow(userId: Long) {
        val followers = api.search(
            SearchFollowerRequest(
                userId = userId,
                followerUserId = requestContext.currentUser()?.id,
            ),
        ).followers
        if (followers.isNotEmpty()) {
            api.delete(followers[0].id)
        }
    }

    fun searchFollowingUserIds(limit: Int = 1000, offset: Int = 0): List<Long> {
        if (!requestContext.toggles().follow || requestContext.currentUser() == null) {
            return emptyList()
        }

        return api.search(
            SearchFollowerRequest(
                followerUserId = requestContext.currentUser()?.id,
                limit = limit,
                offset = offset,
            ),
        ).followers.map { it.userId }
    }

    fun canFollow(userId: Long): Boolean {
        if (!requestContext.toggles().follow || requestContext.currentUser()?.id == userId) {
            return false
        }

        if (requestContext.currentUser() == null) {
            return true
        }

        return !isFollowing(userId)
    }

    fun isFollowing(userId: Long): Boolean {
        val user = requestContext.currentUser() ?: return false

        return api.search(
            SearchFollowerRequest(
                userId = userId,
                followerUserId = user.id,
            ),
        ).followers.isNotEmpty()
    }
}
