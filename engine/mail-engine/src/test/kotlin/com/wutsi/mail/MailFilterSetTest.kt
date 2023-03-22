package com.wutsi.mail

import com.wutsi.mail.filter.CSSFilter
import com.wutsi.mail.filter.DecoratorFilter
import com.wutsi.mail.filter.UTMFilter
import org.junit.jupiter.api.Test

internal class MailFilterSetTest {
    private val context = Fixtures.createMailContext()

    val filter = MailFilterSet(
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
