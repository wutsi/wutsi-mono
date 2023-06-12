package com.wutsi.blog.user.endpoints

import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.service.UserMapper
import com.wutsi.blog.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/users/queries/search")
class SearchUserQuery(
    private val service: UserService,
    private val mapper: UserMapper,
) {
    @PostMapping
    fun execute(@Valid @RequestBody request: SearchUserRequest): SearchUserResponse {
        val users = service.search(request)
        return SearchUserResponse(
            users = users.map { mapper.toUserSummaryDto(it) },
        )
    }
}
