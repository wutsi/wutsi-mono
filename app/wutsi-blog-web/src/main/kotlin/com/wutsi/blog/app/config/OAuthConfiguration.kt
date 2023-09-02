package com.wutsi.blog.app.config

import com.github.scribejava.apis.FacebookApi
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.apis.LinkedInApi20
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth10aService
import com.github.scribejava.core.oauth.OAuth20Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OAuthConfiguration {
    companion object {
        const val GITHUB_OAUTH_SERVICE: String = "GITHUB_OAUTH_SERVICE"
        const val FACEBOOK_OAUTH_SERVICE: String = "FACEBOOK_OAUTH_SERVICE"
        const val GOOGLE_OAUTH_SERVICE: String = "GOOGLE_OAUTH_SERVICE"
        const val TWITTER_OAUTH_SERVICE: String = "TWITTER_OAUTH_SERVICE"
        const val LINKEDIN_OAUTH_SERVICE: String = "LINKEDIN_OAUTH_SERVICE"
        const val YAHOO_OAUTH_SERVICE: String = "YAHOO_OAUTH_SERVICE"
    }

    @Bean(GITHUB_OAUTH_SERVICE)
    fun githubOAuthService(
        @Value("\${wutsi.oauth.github.client-id}") clientId: String,
        @Value("\${wutsi.oauth.github.client-secret}") clientSecret: String,
        @Value("\${wutsi.oauth.github.callback-url}") callbackUrl: String,
    ): OAuth20Service = ServiceBuilder(clientId)
        .apiSecret(clientSecret)
        .callback(callbackUrl)
        .build(GitHubApi.instance())

    @ConditionalOnProperty(value = ["wutsi.toggles.sso-facebook"], havingValue = "true")
    @Bean(FACEBOOK_OAUTH_SERVICE)
    fun facebookOAuthService(
        @Value("\${wutsi.oauth.facebook.client-id}") clientId: String,
        @Value("\${wutsi.oauth.facebook.client-secret}") clientSecret: String,
        @Value("\${wutsi.oauth.facebook.callback-url}") callbackUrl: String,
        @Value("\${wutsi.oauth.facebook.scope}") scope: String,
    ): OAuth20Service = ServiceBuilder(clientId)
        .apiSecret(clientSecret)
        .defaultScope(scope)
        .callback(callbackUrl)
        .build(FacebookApi.instance())

    @ConditionalOnProperty(value = ["wutsi.toggles.sso-google"], havingValue = "true")
    @Bean(GOOGLE_OAUTH_SERVICE)
    fun googleOAuthService(
        @Value("\${wutsi.oauth.google.client-id}") clientId: String,
        @Value("\${wutsi.oauth.google.client-secret}") clientSecret: String,
        @Value("\${wutsi.oauth.google.callback-url}") callbackUrl: String,
        @Value("\${wutsi.oauth.google.scope}") scope: String,
    ): OAuth20Service = ServiceBuilder(clientId)
        .apiSecret(clientSecret)
        .defaultScope(scope)
        .callback(callbackUrl)
        .build(GoogleApi20.instance())

    @ConditionalOnProperty(value = ["wutsi.toggles.sso-linkedin"], havingValue = "true")
    @Bean(LINKEDIN_OAUTH_SERVICE)
    fun linkedinOAuthService(
        @Value("\${wutsi.oauth.linkedin.client-id}") clientId: String,
        @Value("\${wutsi.oauth.linkedin.client-secret}") clientSecret: String,
        @Value("\${wutsi.oauth.linkedin.callback-url}") callbackUrl: String,
        @Value("\${wutsi.oauth.linkedin.scope}") scope: String,
    ): OAuth20Service = ServiceBuilder(clientId)
        .apiSecret(clientSecret)
        .defaultScope(scope)
        .callback(callbackUrl)
        .build(LinkedInApi20.instance())

    @ConditionalOnProperty(value = ["wutsi.toggles.sso-twitter"], havingValue = "true")
    @Bean(TWITTER_OAUTH_SERVICE)
    fun twitterOAuthService(
        @Value("\${wutsi.oauth.twitter.client-id}") clientId: String,
        @Value("\${wutsi.oauth.twitter.client-secret}") clientSecret: String,
        @Value("\${wutsi.oauth.twitter.callback-url}") callbackUrl: String,
    ): OAuth10aService = ServiceBuilder(clientId)
        .apiSecret(clientSecret)
        .callback(callbackUrl)
        .build(TwitterApi.instance())

    @ConditionalOnProperty(value = ["wutsi.toggles.sso-yahoo"], havingValue = "true")
    @Bean(YAHOO_OAUTH_SERVICE)
    fun yahooOAuthService(
        @Value("\${wutsi.oauth.yahoo.client-id}") clientId: String,
        @Value("\${wutsi.oauth.yahoo.client-secret}") clientSecret: String,
        @Value("\${wutsi.oauth.yahoo.callback-url}") callbackUrl: String,
    ): OAuth20Service = ServiceBuilder(clientId)
        .apiSecret(clientSecret)
        .callback(callbackUrl)
        .build(YahooApi20.instance())
}
