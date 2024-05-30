package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import com.wutsi.blog.product.service.LiretamaService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LinkFilterTest {
    private val context = Fixtures.createMailContext(storyId = 1)
    private val liretama = LiretamaService("123")
    private val filter = LinkFilter("https://www.wutsi.com/wclick", liretama)

    @Test
    fun filter() {
        val html = """
            <html>
                <body>
                    <div>
                        <a href="http://www.google.ca">Hello</a>
                    </div>
                    <div>
                        <a href="https://www.yahoo.ca?q=test">World</a>
                    </div>
                    <div>
                        <a href="mailto:foo@gmail.com">email</a>
                    </div>
                    <div>
                        <a href="https://www.liretama.com/livres/un-ete-de-reve?pid=5555555">Un ete de reve</a>
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
                   <a href="https://www.wutsi.com/wclick?utm_medium=email&amp;story-id=1&amp;url=http%3A%2F%2Fwww.google.ca">Hello</a>
                  </div>
                  <div>
                   <a href="https://www.wutsi.com/wclick?utm_medium=email&amp;story-id=1&amp;url=https%3A%2F%2Fwww.yahoo.ca%3Fq%3Dtest">World</a>
                  </div>
                  <div>
                   <a href="mailto:foo@gmail.com">email</a>
                  </div>
                  <div>
                   <a href="https://www.wutsi.com/wclick?utm_medium=email&amp;story-id=1&amp;url=https%3A%2F%2Fwww.liretama.com%2Flivres%2Fun-ete-de-reve%3Fpid%3D123">Un ete de reve</a>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
