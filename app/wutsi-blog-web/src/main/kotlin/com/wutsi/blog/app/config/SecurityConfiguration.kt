package com.wutsi.blog.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.security.oauth.OAuthAuthenticationProvider
import com.wutsi.blog.app.security.oauth.SecurityContextRepositoryImpl
import com.wutsi.blog.app.security.service.AuthenticationSuccessHandlerImpl
import com.wutsi.blog.app.security.servlet.OAuthAuthenticationFilter
import com.wutsi.blog.app.service.AccessTokenStorage
import com.wutsi.blog.app.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val userService: UserService,
    private val authenticationProvider: OAuthAuthenticationProvider,
    private val accessTokenStorage: AccessTokenStorage,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val OAUTH_SIGNIN_PATTERN = "/login/oauth/signin"

        const val PARAM_ACCESS_TOKEN = "token"
        const val PARAM_STATE = "state"
        const val PARAM_USER = "user"

        const val PROVIDER_GITHUB = "github"
        const val PROVIDER_FACEBOOK = "facebook"
        const val PROVIDER_GOOGLE = "google"
        const val PROVIDER_TWITTER = "twitter"
        const val PROVIDER_LINKEDIN = "linkedin"
        const val PROVIDER_YAHOO = "yahoo"
        const val PROVIDER_QA = "qa"
    }

    @Bean
    fun authenticationManager(): AuthenticationManager =
        ProviderManager(authenticationProvider)

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { customizer ->
                customizer
                    .requestMatchers(AntPathRequestMatcher("/me")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/me/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/editor")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/editor/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/subscribe")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/partner/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/stats")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/stats/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/create")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/create/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/pin/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/comments")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/inbox")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/comments/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/@/*/subscribe")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/attachment/download")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/upload", HttpMethod.POST.name())).authenticated()
                    .anyRequest().permitAll()
            }
            .addFilterBefore(
                authenticationFilter(),
                AnonymousAuthenticationFilter::class.java,
            )
            .securityContext { customizer ->
                customizer.securityContextRepository(securityContextRepository())
            }
            .formLogin { customizer ->
                customizer
                    .loginPage("/login")
                    .successHandler(successHandler())
            }
            .build()

    @Bean
    fun authenticationFilter(): OAuthAuthenticationFilter {
        val filter = OAuthAuthenticationFilter(objectMapper, securityContextRepository(), OAUTH_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManager())
        filter.setAuthenticationSuccessHandler(successHandler())
        return filter
    }

    @Bean
    fun securityContextRepository(): SecurityContextRepository =
        SecurityContextRepositoryImpl(accessTokenStorage)

    @Bean
    fun successHandler(): AuthenticationSuccessHandler = AuthenticationSuccessHandlerImpl(userService)
}
