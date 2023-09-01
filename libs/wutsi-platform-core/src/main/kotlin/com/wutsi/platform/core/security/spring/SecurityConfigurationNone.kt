package com.wutsi.platform.core.security.spring

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.security.type"],
    havingValue = "none",
)
open class SecurityConfigurationNone : AbstractWebSecurityConfiguration() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SecurityConfigurationNone::class.java)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        LOGGER.info("Configuring HttpSecurity")
        return http
            .csrf { customizer ->
                customizer.disable()
            }
            .sessionManagement { customizer ->
                customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { customizer ->
                customizer.anyRequest().permitAll()
            }
            .build()
    }
}
