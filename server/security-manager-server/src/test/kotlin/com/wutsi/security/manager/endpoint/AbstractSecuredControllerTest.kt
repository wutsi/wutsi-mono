package com.wutsi.security.manager.endpoint

import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

abstract class AbstractSecuredControllerTest : AbstractControllerTest() {
    override fun createRestTemplate(accountId: Long): RestTemplate {
        val rest = super.createRestTemplate(accountId)
        rest.interceptors.add(createAuthorizationInterceptor(accountId))
        return rest
    }

    private fun createAuthorizationInterceptor(accountId: Long): ClientHttpRequestInterceptor {
        val tokenProvider = TestTokenProvider(
            JWTBuilder(
                subject = accountId.toString(),
                name = "Ray Sponsible",
                subjectType = USER,
                keyProvider = TestRSAKeyProvider(),
            ).build(),
        )

        return SpringAuthorizationRequestInterceptor(tokenProvider)
    }
}
