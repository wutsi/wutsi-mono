package com.wutsi.blog.app.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class WebTokenProviderTest {
    val session = mock<CurrentSessionHolder>()
    val provider = WebTokenProvider(session)

    @Test
    fun getToken() {
        doReturn("token").whenever(session).accessToken()

        assertEquals("token", provider.getToken())
    }
}
