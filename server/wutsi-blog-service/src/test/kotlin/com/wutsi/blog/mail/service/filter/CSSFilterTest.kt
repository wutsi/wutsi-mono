package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class CSSFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = CSSFilter()

    @Test
    fun h1() {
        val html = """
            <html>
                <body>
                    <h1>Hello</b>
                </body>
            </html>
        """.trimIndent()

        val result = filter.filter(html, context)

        assertEquals(
            """
                <html>
                  <head></head>
                  <body>
                    <h1 style="font-size: 1.75em;">
                      Hello
                    </h1>
                  </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
