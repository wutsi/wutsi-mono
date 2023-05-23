package com.wutsi.blog.app.security.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.config.SecurityConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuthAuthenticationFilter(
    pattern: String,
) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(pattern)) {
    @Autowired
    lateinit var mapper: ObjectMapper

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val accessToken = getRequiredParameter(SecurityConfiguration.PARAM_ACCESS_TOKEN, request)
        val user = getUserAttributes(request)
        return authenticationManager.authenticate(
            OAuthTokenAuthentication(
                principal = OAuthPrincipal(accessToken, user),
                accessToken = accessToken,
            ),
        )
    }

    private fun getUserAttributes(request: HttpServletRequest): OAuthUser {
        val user = getRequiredParameter(SecurityConfiguration.PARAM_USER, request)
        return mapper.readValue(user, OAuthUser::class.java)
    }

    private fun getRequiredParameter(name: String, request: HttpServletRequest): String {
        val value = request.getParameter(name)
        if (value == null || value.isEmpty()) {
            throw AuthenticationServiceException("Parameter is missing: $name")
        }
        return value
    }
}
