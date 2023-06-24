package com.wutsi.blog.app.service.ejs.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.platform.core.image.ImageService
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ImageFilterTest {
    private lateinit var imageKitService: ImageService
    private lateinit var requestContext: RequestContext

    private lateinit var filter: ImageEJSFilter

    @BeforeEach
    fun setUp() {
        imageKitService = mock()
        requestContext = mock()

        filter = ImageEJSFilter(imageKitService, requestContext, 960, 400)
        doReturn(false).whenever(requestContext).isMobileUserAgent()
        doReturn("bar.gif").whenever(imageKitService).transform(any(), any())
    }

    @Test
    fun `resize large image on mobile`() {
        doReturn(true).whenever(requestContext).isMobileUserAgent()
        doReturn("bar.gif").whenever(imageKitService).transform(any(), any())

        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='1024' height='200'/>world</body>")
        filter.filter(doc)

        assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  Hello <img src="bar.gif" width="400" loading="lazy">world
                 </body>
                </html>
            """.trimIndent(),
            doc.html(),
        )
    }

    @Test
    fun `no not resize small image on mobile`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='300' height='200'/>world</body>")
        filter.filter(doc)

        assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  Hello <img src width="300" height="200" loading="lazy">world
                 </body>
                </html>
            """.trimIndent(),
            doc.html(),
        )
    }

    @Test
    fun `resize image with large width on desktop`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='1024' height='200'/>world</body>")
        filter.filter(doc)

        assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  Hello <img src="bar.gif" width="960" loading="lazy">world
                 </body>
                </html>
            """.trimIndent(),
            doc.html(),
        )
    }

    @Test
    fun `no not resize small image on desktop`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='200' height='168'/>world</body>")
        filter.filter(doc)

        assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  Hello <img src width="200" height="168" loading="lazy">world
                 </body>
                </html>
            """.trimIndent(),
            doc.html(),
        )
    }
}
