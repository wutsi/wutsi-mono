package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

class AdsEJSFilterTest {
    private val filter = AdsEJSFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse(
            """
              <div>
                <div class='ad'></div>
                <div class='file'>
                  <div class='filename'>img.png</div>
                  <div class='filesize'>14.4 Mb</div>
                </div>
                <div class='ad'></div>
              </div>
            """.trimIndent(),
        )
        filter.filter(StoryModel(id = 1234, user = UserModel(id = 777)), doc)

        kotlin.test.assertEquals(
            """
                <html>
                 <head></head>
                 <body>
                  <div>
                   <div class="ad ads-banner-container" wutsi-ads-type="BOX" wutsi-ads-blog-id="777"></div>
                   <div class="file">
                    <div class="filename">
                     img.png
                    </div>
                    <div class="filesize">
                     14.4 Mb
                    </div>
                   </div>
                   <div class="ad ads-banner-container" wutsi-ads-type="BOX" wutsi-ads-blog-id="777"></div>
                  </div>
                 </body>
                </html>
            """.trimIndent(),
            doc.html(),
        )
    }
}
