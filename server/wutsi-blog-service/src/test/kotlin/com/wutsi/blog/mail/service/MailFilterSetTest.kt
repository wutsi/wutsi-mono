package com.wutsi.blog.mail.service

import com.wutsi.blog.Fixtures
import com.wutsi.blog.mail.service.filter.CSSFilter
import com.wutsi.blog.mail.service.filter.DecoratorFilter
import com.wutsi.blog.mail.service.filter.UTMFilter
import org.junit.jupiter.api.Test

internal class MailFilterSetTest {
    private val context = Fixtures.createMailContext()
    private val filter = MailFilterSet(
        filters = listOf(
            DecoratorFilter(),
            CSSFilter(),
            UTMFilter(),
        ),
    )

    @Test
    fun filter() {
        val html = """
            <p>Hello world</p>
        """.trimIndent()

        val result = filter.filter(html, context)
        println(result)
    }
}
