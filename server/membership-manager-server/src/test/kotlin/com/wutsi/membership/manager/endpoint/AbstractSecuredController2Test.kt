package com.wutsi.membership.manager.endpoint

import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.client.ClientHttpRequestInterceptor

abstract class AbstractSecuredController2Test : AbstractController2Test() {
    companion object {
        const val ACCOUNT_ID = 555L
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        rest.interceptors.add(createAuthorizationInterceptor(ACCOUNT_ID))
    }

    private fun createAuthorizationInterceptor(accountId: Long): ClientHttpRequestInterceptor {
        val tokenProvider = TestTokenProvider(
            JWTBuilder(
                subject = accountId.toString(),
                name = "Account",
                subjectType = USER,
                keyProvider = TestRSAKeyProvider(),
            ).build(),
        )

        return SpringAuthorizationRequestInterceptor(tokenProvider)
    }
}
