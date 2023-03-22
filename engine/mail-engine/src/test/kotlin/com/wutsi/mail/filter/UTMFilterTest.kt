package com.wutsi.mail.filter

import com.wutsi.mail.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UTMFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = UTMFilter()

    @Test
    fun utmSource() {
        val html = """
            <html>
                <body>
                    <a href="https://www.google.ca">Hello</a>
                    <a href="https://www.yahoo.ca?q=test">World</a>
                </body>
            </html>
        """.trimIndent()

        val result = filter.filter(html, context)

        assertEquals(
            """
                <html>
                    <body>
                        <a href="https://www.google.ca?utm_medium=email">Hello</a>
                        <a href="https://www.yahoo.ca?q=test&utm_medium=email">World</a>
                    </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
