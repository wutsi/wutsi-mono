package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Test

internal class VideoFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = VideoFilter("https://www.wutsi.com")

    @Test
    fun filter() {
        val html = """
            <html>
                <body>
                    <div class="player">
                        <img src="https://img.com/1.png" />
                        <div>
                            <span class="play-icon"></span>
                            <span>Play on YouTube</span>
                        </div>
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
                  <div class="player border">
                   <img src="https://img.com/1.png">
                   <div class="border-top">
                    <span class="play-icon"><img width="32" src="https://www.wutsi.com/assets/wutsi/img/play.png" style="vertical-align: middle"></span> <span>Play on YouTube</span>
                   </div>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
