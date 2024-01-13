package com.wutsi.blog.mail.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.Fixtures
import com.wutsi.platform.core.image.ImageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ImageFilterTest {
    private val context = Fixtures.createMailContext()
    private val imageService = mock<ImageService>()
    private val filter = ImageFilter(imageService)

    @Test
    fun filter() {
        // GIVEN
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

        doReturn("/11.png").whenever(imageService).transform(any(), anyOrNull())

        // WHEN
        val result = filter.filter(html, context)

        // THEN
        assertEquals(
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
                     <img src="/11.png" style="max-width: 100%; margin: 0 auto;">
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
