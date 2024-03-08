package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LinkEJSFilterTest {
    private val filter = LinkEJSFilter("https://www.wutsi.com")

    @Test
    fun filterExternal() {
        val doc = Jsoup.parse("<body>Hello <a href='https://www.google.ca/foo.html'>world</a></body>")
        filter.filter(StoryModel(id = 11), doc)

        doc.select("a").forEach {
            assertEquals(
                "https://www.wutsi.com/wclick?story-id=11&url=https%3A%2F%2Fwww.google.ca%2Ffoo.html",
                it.attr("href")
            )
            assertEquals("nofollow", it.attr("rel"))
        }
    }

    @Test
    fun filterEmail() {
        val doc = Jsoup.parse("<body>Hello <a href='mailto: foo@gmail.com'>world</a></body>")
        filter.filter(StoryModel(id = 11), doc)

        doc.select("a").forEach {
            assertEquals("mailto: foo@gmail.com", it.attr("href"))
        }
    }
}
