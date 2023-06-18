package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test

internal class ImageFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = ImageFilter()

    @Test
    fun filter() {
        val html = """
            <html>
                <body>
                    <figure>
                        <img src="/1.png" width="100" height="100" />
                    </figure>

                    <div class="story-content">
                        <h1>Hello</b>
                        <figure>
                            <img src="/1.png" width="100" height="100" />
                            <figcaption>This is the caption</figcaption>
                        </figure>
                    </div>
                </body>
            </html>
        """.trimIndent()

        val result = filter.filter(html, context)

        kotlin.test.assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  <figure>
                   <img src="/1.png" width="100" height="100">
                  </figure>
                  <div class="story-content">
                   <h1>Hello
                    <figure style="margin: 0; text-align: center">
                     <img src="/1.png" style="max-width: 100%; margin: 0 auto;">
                     <figcaption style="text-decoration: underline; font-size: 0.8em;">
                      This is the caption
                     </figcaption>
                    </figure></h1>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
