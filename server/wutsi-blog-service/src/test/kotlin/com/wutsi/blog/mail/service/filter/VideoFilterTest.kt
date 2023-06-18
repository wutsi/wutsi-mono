package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test

internal class VideoFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = VideoFilter("https://www.wutsi.com/assets")

    @Test
    fun filter() {
        val html = """
            <html>
                <body>
                    <div class="play-icon">
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
                  <div class="play-icon">
                   <img width="32" src="https://www.wutsi.com/assets/assets/wutsi/img/mail/play-video.png" style="vertical-align: middle">
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
