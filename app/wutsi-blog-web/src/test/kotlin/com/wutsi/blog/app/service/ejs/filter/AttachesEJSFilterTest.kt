package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

internal class AttachesEJSFilterTest {
    private val filter = AttachesEJSFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse(
            """
                <a href='https://www.google.ca/img.png' title='img.png' class='attaches'>
                  <div class='attaches'>
                    <div class='ext'><span class='png'>PNG</span></div>
                    <div class='file'>
                      <div class='filename'>img.png</div>
                      <div class='filesize'>14.4 Mb</div>
                    </div>
                  </div>
                </a>
            """.trimIndent(),
        )
        filter.filter(StoryModel(id = 1234), doc)

        kotlin.test.assertEquals(
            """
                <html>
                 <head>
                 </head>
                 <body>
                  <a href="/attachment/download?f=img.png&amp;l=aHR0cHM6Ly93d3cuZ29vZ2xlLmNhL2ltZy5wbmc%3D&amp;s=1234" title="img.png" class="attaches">
                   <div class="attaches">
                    <div class="ext">
                     <span class="png">PNG</span>
                    </div>
                    <div class="file">
                     <div class="filename">
                      img.png
                     </div>
                     <div class="filesize">
                      14.4 Mb
                     </div>
                    </div>
                   </div></a>
                 </body>
                </html>
            """.trimIndent(),
            doc.html(),
        )
    }
}
