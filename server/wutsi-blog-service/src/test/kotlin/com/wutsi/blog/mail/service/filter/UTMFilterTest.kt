package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UTMFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = UTMFilter()

    @Test
    fun utmSource() {
        val html = """
            <html>
                <body>
                    <div>
                        <a href="https://www.google.ca">Hello</a>
                    </div>
                    <div>
                        <a href="https://www.yahoo.ca?q=test">World</a>
                    </div>
                </body>
            </html>
        """.trimIndent()

        val result = filter.filter(html, context)

        assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  <div>
                   <a href="https://www.google.ca?utm_medium=email">Hello</a>
                  </div>
                  <div>
                   <a href="https://www.yahoo.ca?q=test&amp;utm_medium=email">World</a>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
