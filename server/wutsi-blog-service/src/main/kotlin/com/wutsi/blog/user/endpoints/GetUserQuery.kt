package com.wutsi.blog.user.endpoints

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.service.UserMapper
import com.wutsi.blog.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetUserQuery(
    private val service: UserService,
    private val subscriptionService: SubscriptionService,
    private val mapper: UserMapper,
    private val securityManager: SecurityManager,
) {
    @GetMapping("/v1/users/{id}")
    fun execute(@PathVariable id: Long): GetUserResponse {
        val user = service.findById(id)
        return find(user)
    }

    @GetMapping("/v1/users/@/{name}")
    fun get(@PathVariable name: String): GetUserResponse {
        val user = service.findByName(name)
        return find(user)
    }

    private fun find(user: UserEntity): GetUserResponse {
        val userIds = listOf(user.id!!)
        val subscriptions = subscriptionService.findSubscriptions(userIds, securityManager.getCurrentUserId())

        return GetUserResponse(
            user = mapper.toUserDto(
                user,
                subscriptions.firstOrNull(),
            ),
        )
    }
}
