package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.platform.core.stream.EventStream
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset

abstract class AbstractControllerTest {
    @MockBean
    protected lateinit var marketplaceAccessApi: MarketplaceAccessApi

    @MockBean
    protected lateinit var membershipAccessApi: MembershipAccessApi

    @MockBean
    protected lateinit var eventStream: EventStream

    protected var rest = RestTemplate()

    protected fun createFeignNotFoundException(code: String) = FeignException.NotFound(
        "",
        createFeignRequest(),
        createFeignBody(code),
        emptyMap(),
    )

    protected fun createFeignConflictException(code: String) = FeignException.Conflict(
        "",
        createFeignRequest(),
        createFeignBody(code),
        emptyMap(),
    )

    private fun createFeignRequest() = Request.create(
        Request.HttpMethod.POST,
        "https://www.google.ca",
        emptyMap(),
        "".toByteArray(),
        Charset.defaultCharset(),
        RequestTemplate(),
    )

    private fun createFeignBody(code: String) =
        """
            {
                "error":{
                    "code": "$code"
                }
            }
        """.trimIndent().toByteArray()
}
