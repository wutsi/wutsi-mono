package com.wutsi.application

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import org.junit.jupiter.api.BeforeEach

abstract class AbstractSecuredEndpointTest : AbstractEndpointTest() {
    companion object {
        const val MEMBER_ID = 1000L
    }

    var member = Fixtures.createMember(id = MEMBER_ID)

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest.interceptors.add(
            SpringAuthorizationRequestInterceptor(
                TestTokenProvider(
                    JWTBuilder(
                        subject = MEMBER_ID.toString(),
                        name = "Ray Sponsible",
                        subjectType = SubjectType.USER,
                        keyProvider = TestRSAKeyProvider(),
                        admin = false,
                    ).build(),
                ),
            ),
        )

        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())
    }
}
