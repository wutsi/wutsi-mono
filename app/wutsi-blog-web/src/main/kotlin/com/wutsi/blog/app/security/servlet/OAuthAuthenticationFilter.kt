package com.wutsi.blog.app.security.servlet

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthPrincipal
import com.wutsi.blog.app.security.oauth.OAuthTokenAuthentication
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.client.HttpClientErrorException

class OAuthAuthenticationFilter(
    private val objectMapper: ObjectMapper,
    private val securityContextRepository: SecurityContextRepository,
    pattern: String,
) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(pattern)) {
    @Autowired
    lateinit var mapper: ObjectMapper

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        try {
            val accessToken = getRequiredParameter(SecurityConfiguration.PARAM_ACCESS_TOKEN, request)
            val user = getUserAttributes(request)
            val authentication = authenticationManager.authenticate(
                OAuthTokenAuthentication(
                    principal = OAuthPrincipal(accessToken, user),
                    accessToken = accessToken,
                ),
            )

            val context = SecurityContextHolder.getContext()
            context.authentication = authentication
            securityContextRepository.saveContext(context, request, response)
            return authentication
        } catch (ex: HttpClientErrorException) {
            val error = toErrorResponse(ex)?.error ?: throw ex
            if (error.code == ErrorCode.USER_SUSPENDED) {
                response.sendRedirect("/error/account_suspended")
            }
            return null
        }
    }

    private fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse? =
        try {
            objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
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
