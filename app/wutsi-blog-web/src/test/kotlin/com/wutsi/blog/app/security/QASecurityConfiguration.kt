package com.wutsi.blog.app.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
class QASecurityConfiguration(
    private val authenticationManager: AuthenticationManager,
    private val successHandler: AuthenticationSuccessHandler,
) {
    companion object {
        const val QA_SIGNIN_PATTERN = "/login/qa/signin"
    }

    @Bean
    fun qaAuthenticationFilter(): QAAuthenticationFilter {
        val filter = QAAuthenticationFilter(QA_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManager)
        filter.setAuthenticationSuccessHandler(successHandler)
        return filter
    }
}
