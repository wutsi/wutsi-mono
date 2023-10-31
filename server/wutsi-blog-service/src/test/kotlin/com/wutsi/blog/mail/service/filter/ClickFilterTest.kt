package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ClickFilterTest {
    private val filter = ClickFilter("https://www.wutsi.com/click")

    @Test
    fun filter() {
        val context = Fixtures.createMailContext(storyId = 111)

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
                      This is a nice <a href="#anchor">anchor</a> with <a href="mailto:foo@a.com">mail</a>
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
                   <a href="https://www.wutsi.com/click?story-id=111&amp;url=http%3A%2F%2Fwww.google.ca">Hello</a>
                  </div>
                  <div>
                   <a href="https://www.wutsi.com/click?story-id=111&amp;url=https%3A%2F%2Fwww.yahoo.ca%3Fq%3Dtest">World</a>
                  </div>
                  <div>
                   This is a nice <a href="#anchor">anchor</a> with <a href="mailto:foo@a.com">mail</a>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result
        )
    }

    @Test
    fun noStory() {
        val context = Fixtures.createMailContext()

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
                      This is a nice <a href="#anchor">anchor</a> with <a href="mailto:foo@a.com">mail</a>
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
                   <a href="https://www.wutsi.com/click?url=http%3A%2F%2Fwww.google.ca">Hello</a>
                  </div>
                  <div>
                   <a href="https://www.wutsi.com/click?url=https%3A%2F%2Fwww.yahoo.ca%3Fq%3Dtest">World</a>
                  </div>
                  <div>
                   This is a nice <a href="#anchor">anchor</a> with <a href="mailto:foo@a.com">mail</a>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result
        )
    }
}
