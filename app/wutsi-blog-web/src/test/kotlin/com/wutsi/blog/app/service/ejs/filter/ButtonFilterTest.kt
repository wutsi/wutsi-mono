package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ButtonFilterTest {
    private val filter = ButtonEJSFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse(
            "<body>" +
                "<div class='button stretched large centered'><a href='http://www.google.ca'>Yo</a></div>" +
                "<div><a href='http://www.yahoo.ca'>Yo</a></div>" +
                "<a href='http://www.msn.ca'>Yo</a>",
        )
        filter.filter(StoryModel(), doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <div class=\"button stretched large centered text-center\">\n" +
                "   <a href=\"http://www.google.ca\" class=\"btn btn-primary btn-lg btn-block\">Yo</a>\n" +
                "  </div>\n" +
                "  <div>\n" +
                "   <a href=\"http://www.yahoo.ca\">Yo</a>\n" +
                "  </div><a href=\"http://www.msn.ca\">Yo</a>\n" +
                " </body>\n" +
                "</html>",
            doc.html(),
        )
    }
}
