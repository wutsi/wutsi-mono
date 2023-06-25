package com.wutsi.blog.app.service.ejs.link

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YouTubeLinkExtractorTest {
    private val rest = mock<RestTemplate>()
    private var extractor = YouTubeLinkExtractor(rest, "x---x")

    @Test
    fun acceptYouTubeVideo() {
        val url = "https://www.youtube.com/watch?v=buS6MIrPBuc"
        assertTrue(extractor.accept(url))
    }

    @Test
    fun acceptInvalid() {
        val url = "https://www.google.ca"
        assertFalse(extractor.accept(url))
    }

    @Test
    fun test() {
        // GIVEN
        doReturn(
            ResponseEntity.ok(
                YTListResponse(
                    items = listOf(
                        YTVideo(
                            id = "buS6MIrPBuc",
                            snippet = YTSnippet(
                                title = "Old School | Funk Mix 80s (113bpm) [Dj'S Bootleg Dance Re-Mix]",
                                description = "This is a nice description",
                                thumbnails = YTThumbnails(
                                    standard = YTAsset("https://i.ytimg.com/vi/buS6MIrPBuc/sddefault.jpg"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).whenever(rest).getForEntity(any<String>(), eq(YTListResponse::class.java))

        // WHEN
        val meta = extractor.extract("https://www.youtube.com/watch?v=buS6MIrPBuc")

        // THEN
        assertEquals("Old School | Funk Mix 80s (113bpm) [Dj'S Bootleg Dance Re-Mix]", meta.title)
        assertEquals("This is a nice description", meta.description)
        assertEquals("YouTube", meta.site_name)
        assertEquals("https://i.ytimg.com/vi/buS6MIrPBuc/sddefault.jpg", meta.image.url)
    }
}
