package com.wutsi.blog.app.service.ejs.filter

import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SubscribeBannerEJSFilterTest {
    private val filter = SubscribeBannerEJSFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse(
            """
<body>
 <div class="button">
  <a href="/@/yo/subscribe?return-url=/">Susbcribe</a>.
 </div>
</body>
            """.trimIndent(),
        )
        filter.filter(doc)

        assertEquals(
            """
<html>
 <head></head>
 <body>
  <div class="button padding subscription-container">
   <a href="/@/yo/subscribe?return-url=/" rel="nofollow">Susbcribe</a>.
  </div>
 </body>
</html>
            """.trimIndent(),
            doc.html().trimIndent(),
        )
    }
}
