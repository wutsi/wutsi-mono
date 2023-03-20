package com.wutsi.security.manager.endpoint

import org.junit.jupiter.api.BeforeEach
import org.springframework.web.client.RestTemplate

abstract class AbstractControllerTest {
    companion object {
        const val ACCOUNT_ID = 100L
    }

    protected var rest = RestTemplate()

    @BeforeEach
    open fun setUp() {
        rest = createRestTemplate(ACCOUNT_ID)
    }

    protected open fun createRestTemplate(accountId: Long) = RestTemplate()
}
