package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.LiretamaService
import com.wutsi.blog.app.service.YouscribeService
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LinkEJSFilterTest {
    private val liretamaService = LiretamaService("123", CountryMapper(""))
    private val youscribeService = YouscribeService()
    private val filter = LinkEJSFilter("https://www.wutsi.com", liretamaService, youscribeService)

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

    @Test
    fun filterLiretamaBook() {
        val doc =
            Jsoup.parse("<body>Hello <a href='https://www.liretama.com/livres/les-joies-de-karma'>world</a></body>")
        filter.filter(StoryModel(id = 11, user = UserModel(id = 1)), doc)

        doc.select("a").forEach {
            assertEquals(
                "https://www.wutsi.com/wclick?story-id=11&url=https%3A%2F%2Fwww.liretama.com%2Flivres%2Fles-joies-de-karma%3Fpid%3D123",
                it.attr("href")
            )
            assertEquals("buy-liretama", it.attr("wutsi-track-event"))
            assertEquals("nofollow", it.attr("rel"))
        }
    }

    @Test
    fun filterLiretama() {
        val doc =
            Jsoup.parse("<body>Hello <a href='https://www.liretama.com/auteur/yo-man'>world</a></body>")
        filter.filter(StoryModel(id = 11), doc)

        doc.select("a").forEach {
            assertEquals(
                "https://www.wutsi.com/wclick?story-id=11&url=https%3A%2F%2Fwww.liretama.com%2Fauteur%2Fyo-man",
                it.attr("href")
            )
            assertEquals("buy-liretama", it.attr("wutsi-track-event"))
            assertEquals("nofollow", it.attr("rel"))
        }
    }

    @Test
    fun filterYouScribe() {
        val doc =
            Jsoup.parse("<body>Hello <a href='https://www.youscribe.com/catalogue/author/nathalie-flore-1808631'>world</a></body>")
        filter.filter(StoryModel(id = 11, user = UserModel(id = 1)), doc)

        doc.select("a").forEach {
            assertEquals(
                "https://www.wutsi.com/wclick?story-id=11&url=https%3A%2F%2Fwww.youscribe.com%2Fcatalogue%2Fauthor%2Fnathalie-flore-1808631",
                it.attr("href")
            )
            assertEquals("buy-youscribe", it.attr("wutsi-track-event"))
            assertEquals("nofollow", it.attr("rel"))
        }
    }
}
