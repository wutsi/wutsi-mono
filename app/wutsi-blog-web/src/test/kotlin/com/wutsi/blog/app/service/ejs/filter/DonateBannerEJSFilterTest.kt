package com.wutsi.blog.app.service.ejs.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DonateBannerEJSFilterTest {
    private val requestContext = mock<RequestContext>()
    private val filter = DonateBannerEJSFilter(requestContext)

    @Test
    fun filter() {
        doReturn("Yo Man").whenever(requestContext).getMessage(any(), anyOrNull(), anyOrNull(), anyOrNull())

        val doc = Jsoup.parse(
            """
<body>
 <div class="button">
  <a href="/@/yo/donate">Susbcribe</a>.
 </div>
</body>
            """.trimIndent(),
        )
        filter.filter(StoryModel(), doc)

        assertEquals(
            """
<html>
 <head></head>
 <body>
  <div class="button padding border donation-container">
   <div class="margin-bottom">
    Yo Man
   </div><a href="/@/yo/donate" rel="nofollow">Susbcribe</a>.
  </div>
 </body>
</html>
            """.trimIndent(),
            doc.html().trimIndent(),
        )
    }
}
