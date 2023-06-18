package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class CSSFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = CSSFilter()

    @Test
    fun filter() {
        val html = """
            <html>
                <body>
                    <h1>Hello</b>
                    <div class="margin-top">Hello</div>
                    <button class="btn btn-primary">Yo</button>
                </body>
            </html>
        """.trimIndent()

        val result = filter.filter(html, context)

        assertEquals(
            """
                <html>
                  <head></head>
                  <body>
                    <h1>
                      Hello
                      <div class="margin-top" style="margin-top: 16px;">
                        Hello
                      </div>
                      <button class="btn btn-primary" style="border-radius: 16px;display: inline-block;font-weight: 400;color: #FFFFFF;background-color: #1D7EDF;text-align: center;vertical-align: middle;border: 1px solid transparent;padding: .375rem .75rem;font-size: 1rem;line-height: 1.5;text-decoration: none;">Yo</button>
                    </h1>
                  </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
