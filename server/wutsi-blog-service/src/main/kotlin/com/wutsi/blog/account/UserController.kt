package com.wutsi.blog.account

import com.wutsi.blog.account.mapper.UserMapper
import com.wutsi.blog.account.service.UserService
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.SortOrder.ascending
import com.wutsi.blog.client.event.UpdateUserEvent
import com.wutsi.blog.client.user.CountUserResponse
import com.wutsi.blog.client.user.GetUserResponse
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.SearchUserResponse
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.client.user.UpdateUserAttributeResponse
import com.wutsi.blog.client.user.UserSortStrategy
import com.wutsi.blog.client.user.UserSortStrategy.created
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class UserController(
    private val service: UserService,
    private val mapper: UserMapper,
    private val events: ApplicationEventPublisher,
) {
    @GetMapping("/v1/user/{id}")
    fun get1(@PathVariable id: Long) = get(id)

    @GetMapping("/v1/users/{id}")
    fun get(@PathVariable id: Long): GetUserResponse {
        val user = service.findById(id)
        return GetUserResponse(
            user = mapper.toUserDto(user),
        )
    }

    @GetMapping("/v1/users/@/{name}")
    fun get(@PathVariable name: String): GetUserResponse {
        val user = service.findByName(name)
        return GetUserResponse(
            user = mapper.toUserDto(user),
        )
    }

    @PostMapping("/v1/users/{id}/attributes")
    fun set(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserAttributeRequest,
    ): UpdateUserAttributeResponse {
        service.set(id, request)

        events.publishEvent(
            UpdateUserEvent(
                userId = id,
                name = request.name!!,
                value = request.value,
            ),
        )

        return UpdateUserAttributeResponse(
            userId = id,
        )
    }

    @GetMapping("/v1/users")
    fun search(
        @RequestParam(required = false) autoFollowedByBlogs: Boolean? = null,
        @RequestParam(required = false) blog: Boolean? = null,
        @RequestParam(required = false) userId: List<Long>? = null,
        @RequestParam(required = false) sortBy: UserSortStrategy? = null,
        @RequestParam(required = false) sortOrder: SortOrder? = null,
        @RequestParam(required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(required = false, defaultValue = "0") offset: Int = 0,
    ): SearchUserResponse {
        val users = service.search(
            SearchUserRequest(
                autoFollowedByBlogs = autoFollowedByBlogs,
                blog = blog,
                userIds = userId?.let { it } ?: emptyList(),
                sortBy = sortBy?.let { it } ?: created,
                sortOrder = sortOrder?.let { it } ?: ascending,
                limit = limit,
                offset = offset,
            ),
        )
        return SearchUserResponse(
            users = users.map { mapper.toUserSummaryDto(it) },
        )
    }

    @GetMapping("/v1/users/count")
    fun count(
        @RequestParam(required = false) autoFollowedByBlogs: Boolean? = null,
        @RequestParam(required = false) blog: Boolean? = null,
        @RequestParam(required = false) userId: List<Long>? = null,
    ): CountUserResponse {
        val total = service.count(
            SearchUserRequest(
                autoFollowedByBlogs = autoFollowedByBlogs,
                blog = blog,
                userIds = userId?.let { it } ?: emptyList(),
            ),
        )
        return CountUserResponse(total = total.toInt())
    }

    @PostMapping("/v1/users/search")
    fun search(
        @RequestBody request: SearchUserRequest,
    ): SearchUserResponse =
        SearchUserResponse(
            users = service.search(request).map { mapper.toUserSummaryDto(it) },
        )
}
