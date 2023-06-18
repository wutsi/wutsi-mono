package com.wutsi.blog.mail.service

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MailFilterSetTest {
    private val context = Fixtures.createMailContext()

    @Test
    fun filter() {
        val filter1 = mock<MailFilter>()
        doReturn("A").whenever(filter1).filter(anyOrNull(), anyOrNull())

        val filter2 = mock<MailFilter>()
        doReturn("AB").whenever(filter2).filter(anyOrNull(), anyOrNull())

        val filter = MailFilterSet(listOf(filter1, filter2))

        val result = filter.filter("<p>Hello</b>", context)

        verify(filter1).filter("<p>Hello</b>", context)
        verify(filter2).filter("A", context)
        assertEquals("AB", result)
    }
}
