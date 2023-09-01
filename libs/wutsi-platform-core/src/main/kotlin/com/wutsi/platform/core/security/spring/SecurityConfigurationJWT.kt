package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.core.security.spring.jwt.JWTAuthenticationFilter
import com.wutsi.platform.core.security.spring.jwt.JWTAuthenticationProvider
import jakarta.servlet.Filter
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.security.type"],
    havingValue = "jwt",
    matchIfMissing = true,
)
@ConfigurationProperties(prefix = "wutsi.platform.security")
open class SecurityConfigurationJWT(
    private val tokenProvider: TokenProvider,
    private val context: ApplicationContext,
) : AbstractWebSecurityConfiguration() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SecurityConfigurationJWT::class.java)
    }

    var publicEndpoints: List<String> = emptyList()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val publicEndpoints = publicEndpoints()
        LOGGER.info("Configuring HttpSecurity")
        LOGGER.info(" Public Endpoints=$publicEndpoints")

        return http
            .csrf { cfg ->
                cfg.disable()
            }.authorizeHttpRequests { authz ->
                authz.requestMatchers(publicEndpoints).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                authenticationFilter(),
                AnonymousAuthenticationFilter::class.java,
            )
            .sessionManagement { cfg ->
                cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(JWTAuthenticationProvider())
            .addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter::class.java).build()
    }

    private fun authenticationFilter(): Filter =
        JWTAuthenticationFilter(requestMatcher = securedEndpoints())

    private fun securedEndpoints(): RequestMatcher =
        NegatedRequestMatcher(
            publicEndpoints(),
        )

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
                        HttpMethod.valueOf(parts[0].uppercase()).name(),
                    ),
                )
            } else {
                throw IllegalStateException("Expected format: <METHOD> <PATH>. Got: $it")
            }
        }
        return OrRequestMatcher(matchers)
    }
}
