package com.wutsi.blog.app.service.ejs.link

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.extractor.DescriptionExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TitleExtractor
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultLinkExtractorTest {
    private val downloader = mock<Downloader>()
    private val extractor = DefaultLinkExtractor(
        downloader = downloader,
        titleExtractor = TitleExtractor(),
        descriptionExtractor = DescriptionExtractor(),
        imageExtractor = ImageExtractor(),
        siteNameExtractor = SiteNameExtractor(),
    )

    @Test
    fun acceptYouTubeVideo() {
        val url = "https://www.youtube.com/watch?v=buS6MIrPBuc"
        assertTrue(extractor.accept(url))
    }

    @Test
    fun acceptAny() {
        val url = "https://www.google.ca"
        assertTrue(extractor.accept(url))
    }

    @Test
    fun test() {
        // GIVEN
        val html = IOUtils.toString(DefaultLinkExtractorTest::class.java.getResourceAsStream("/kamerkongossa.html"))
        doReturn(html).whenever(downloader).download(any())

        // THEN
        val meta = extractor.extract("http://localhost:8080/kamerkongossa.html")

        // THEN
        assertEquals(
            "Yaoundé: on rencontre le sous-développement par les chemins qu’on emprunte pour l’éviter - Kamer Kongossa",
            meta.title
        )
        assertEquals(
            "Mon bonjour glisse sur les trois premières sans faire de bruit ni obtenir de réponses, . La quatrième, maugrée un charabia inaudible et se lève en tchuipant",
            meta.description
        )
        assertEquals("Kamer Kongossa", meta.site_name)
        assertEquals("https://kamerkongossa.cm/wp-content/uploads/2020/01/bain-de-boue.jpg", meta.image.url)
    }
}
