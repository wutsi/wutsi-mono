package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test

internal class ButtonFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = ButtonFilter()

    @Test
    fun filter() {
        val html = """
            <html>
                <body class="story-content">
                    <div class="button">
                        <a href="https://www.google.com">
                    </figure>
                </body>
            </html>
        """.trimIndent()

        val result = filter.filter(html, context)

        kotlin.test.assertEquals(
            """
                <html>
                 <head></head>
                 <body class="story-content">
                  <div class="button margin-top margin-bottom text-center">
                   <a href="https://www.google.com" class="btn-primary"> </a>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
