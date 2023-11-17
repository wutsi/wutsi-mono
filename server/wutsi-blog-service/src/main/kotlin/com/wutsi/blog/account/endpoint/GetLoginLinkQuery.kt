package com.wutsi.blog.account.endpoint

import com.wutsi.blog.account.dto.GetLoginLinkResponse
import com.wutsi.blog.account.dto.Link
import com.wutsi.blog.account.service.LoginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetLoginLinkQuery(
    private val service: LoginService,
) {
    @GetMapping("/v1/auth/links/{id}")
    fun get(@PathVariable id: String): GetLoginLinkResponse {
        val data = service.getLoginLink(id)
        return GetLoginLinkResponse(
            link = Link(
                email = data.email,
                referer = data.referer,
                redirectUrl = data.redirectUrl,
                storyId = data.storyId,
                language = data.language,
            )
        )
    }
}
