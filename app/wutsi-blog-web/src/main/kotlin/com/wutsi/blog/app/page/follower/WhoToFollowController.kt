package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.user.SearchUserRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/follow/who")
class WhoToFollowController(
    private val followerService: FollowerService,
    private val userService: UserService,
    private val requestContext: RequestContext,
) {
    @GetMapping()
    fun who(
        @RequestParam(required = false, defaultValue = "3") limit: Int = 3,
        model: Model,
    ): String {
        val followingUserIds = followerService.searchFollowingUserIds()
        val whoToFollow = findWhoToFollow(followingUserIds, limit)

        model.addAttribute("whoToFollow", whoToFollow)
        return "page/follow/who"
    }

    private fun findWhoToFollow(followingUserIds: List<Long>, limit: Int): List<UserModel> {
        val result = mutableListOf<UserModel>()
        result.addAll(findPreferredAuthors(followingUserIds, limit))
        if (result.size < limit) {
            result.addAll(findAuthors(followingUserIds, limit - result.size))
        }

        return result
    }

    private fun findPreferredAuthors(followingUserIds: List<Long>, limit: Int): List<UserModel> {
        if (followingUserIds.isEmpty()) {
            return emptyList()
        }

        val userIds = followingUserIds
            .shuffled()
            .take(limit)
        return userService.search(
            request = SearchUserRequest(
                userIds = userIds,
                limit = limit,
            ),
        )
    }

    private fun findAuthors(followingUserIds: List<Long>, limit: Int): List<UserModel> {
        val userId = requestContext.currentUser()?.id
        val users = userService.search(
            SearchUserRequest(
                blog = true,
            ),
        )

        return users.filter { !followingUserIds.contains(it.id) && it.id != userId }
            .shuffled()
            .take(limit)
    }
}
