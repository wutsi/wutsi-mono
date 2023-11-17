package com.wutsi.blog.app.service

import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.CreateLoginLinkResponse
import com.wutsi.blog.account.dto.GetLoginLinkResponse
import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.app.backend.AuthenticationBackend
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class AuthenticationService(
    private val backend: AuthenticationBackend,
    private val requestContext: RequestContext,
) {
    fun runAs(userName: String) {
        backend.loginAs(
            LoginUserAsCommand(
                userName = userName,
                accessToken = requestContext.accessToken()!!,
            ),
        )
    }

    fun login(request: LoginUserCommand): LoginUserResponse =
        backend.login(request)

    fun loginUrl(url: String, redirectUrl: String?, storyId: Long?, referer: String?): String {
        val params = mutableListOf<String>()
        redirectUrl?.let { params.add("redirect=" + URLEncoder.encode(redirectUrl, "utf-8")) }
        storyId?.let { params.add("story-id=$storyId") }
        referer?.let { params.add("referer=$referer") }

        return if (redirectUrl == null) url else "$url?" + params.joinToString("&")
    }

    fun createEmailLink(request: CreateLoginLinkCommand): CreateLoginLinkResponse =
        backend.createLink(request)

    fun getLink(id: String): GetLoginLinkResponse =
        backend.getLink(id)
}
