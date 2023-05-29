package com.wutsi.blog.app.config

import com.wutsi.blog.app.security.oauth.OAuthAuthenticationFilter
import com.wutsi.blog.app.security.oauth.OAuthRememberMeService
import com.wutsi.blog.app.security.qa.QAAuthenticationFilter
import com.wutsi.blog.app.security.service.AuthenticationSuccessHandlerImpl
import com.wutsi.blog.app.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
//        private val accessTokenStorage: AccessTokenStorage,
//        private val oauthAuthenticationProvider: OAuthAuthenticationProvider,
    private val oAuthRememberMeService: OAuthRememberMeService,
    private val userService: UserService,
    // private val autoLoginAuthenticationProvider: AutoLoginAuthenticationProvider
) : WebSecurityConfigurerAdapter() {
    companion object {
        const val OAUTH_SIGNIN_PATTERN = "/login/oauth/signin"
        const val QA_SIGNIN_PATTERN = "/login/qa/signin"

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

//    @Autowired
//    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.authenticationProvider(oauthAuthenticationProvider)
//            .authenticationProvider(autoLoginAuthenticationProvider)
//    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/me").authenticated()
            .antMatchers("/me/**/*").authenticated()
            .antMatchers("/editor").authenticated()
            .antMatchers("/editor/**/*").authenticated()
            .antMatchers("/subscribe").authenticated()
            .antMatchers("/partner/**/*").authenticated()
            .antMatchers("/stats").authenticated()
            .antMatchers("/stats/**/*").authenticated()
            .antMatchers("/create").authenticated()
            .antMatchers("/create/**/*").authenticated()
            .antMatchers("/pin/**/*").authenticated()
            .antMatchers("/comments").authenticated()
            .antMatchers("/comments/**/*").authenticated()
            .antMatchers(HttpMethod.POST, "/upload").authenticated()
            .anyRequest().permitAll()
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .successHandler(successHandler())
    }

    @Bean
    fun authenticationFilter(): OAuthAuthenticationFilter {
        val filter = OAuthAuthenticationFilter(OAUTH_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManagerBean())
        filter.rememberMeServices = oAuthRememberMeService
        filter.setAuthenticationSuccessHandler(successHandler())
        return filter
    }

    @Bean
    @ConditionalOnProperty(name = ["wutsi.toggles.qa-login"], havingValue = "true")
    fun qaAuthenticationFilter(): QAAuthenticationFilter {
        val filter = QAAuthenticationFilter(QA_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManagerBean())
        filter.rememberMeServices = oAuthRememberMeService
        filter.setAuthenticationSuccessHandler(successHandler())
        return filter
    }

    @Bean
    fun successHandler(): AuthenticationSuccessHandler = AuthenticationSuccessHandlerImpl(userService)

//    @Bean
//    fun autoLoginAuthenticationFilter(): Filter = AutoLoginAuthenticationFilter(
//            storage = accessTokenStorage,
//            authenticationManager = authenticationManagerBean(),
//            excludePaths = OrRequestMatcher(
//                    AntPathRequestMatcher("/login"),
//                    AntPathRequestMatcher("/login/**/*"),
//                    AntPathRequestMatcher("/logout"),
//                    AntPathRequestMatcher("/assets/**/*"),
//                    AntPathRequestMatcher("*.ico")
//            )
//    )
}
