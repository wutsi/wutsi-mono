package com.wutsi.application.config

import com.wutsi.application.servlet.TokenVerifierFilter
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.security.TokenBlacklistService
import com.wutsi.platform.core.security.TokenProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.time.Clock

@Configuration
@ConfigurationProperties(prefix = "wutsi.platform.security")
class TokenVerifierConfiguration(
    private val blacklistService: TokenBlacklistService,
    private val tokenProvider: TokenProvider,
    private val clock: Clock,
    private val logger: KVLogger,
) {
    var publicEndpoints: List<String> = emptyList()

    @Bean
    fun tokenVerifierFilter() = TokenVerifierFilter(
        blacklistService,
        tokenProvider,
        securedEndpoints(),
        clock,
        logger,
    )

    private fun securedEndpoints(): RequestMatcher =
        NegatedRequestMatcher(publicEndpoints())

    private fun publicEndpoints(): RequestMatcher {
        val matchers = mutableListOf<RequestMatcher>(
            AntPathRequestMatcher("/actuator/**", "GET"),
            AntPathRequestMatcher("/**", "OPTIONS"),
        )

        publicEndpoints.forEach {
            val parts = it.split("\\s+".toRegex())
            if (parts.size == 2) {
                matchers.add(
                    AntPathRequestMatcher(
                        parts[1],
                        HttpMethod.valueOf(parts[0].uppercase()).name,
                    ),
                )
            } else {
                throw IllegalStateException("Expected format: <METHOD> <PATH>. Got: $it")
            }
        }
        return OrRequestMatcher(matchers)
    }
}
