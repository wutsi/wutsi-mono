package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.model.SitemapModel
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import jakarta.xml.bind.JAXB
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SitemapControllerTest {
    @LocalServerPort
    private val port: Int = 0

    @MockBean
    protected lateinit var userBackend: UserBackend

    @MockBean
    protected lateinit var storyBackend: StoryBackend

    @BeforeEach
    fun setUp() {
        doReturn(
            SearchUserResponse(
                users = listOf(
                    UserSummary(id = 1, name = "ray.sponsible"),
                    UserSummary(id = 2, name = "roger.milla"),
                    UserSummary(id = 2, name = "samuel.etoo"),
                ),
            ),
        ).whenever(userBackend).search(any())

        doReturn(
            SearchStoryResponse(
                stories = listOf(
                    StorySummary(id = 43800, slug = "/read/43800/this-is-a-problem"),
                    StorySummary(id = 12342, slug = "/read/12342/roger-milla-marque-10-buts"),
                ),
            ),
        ).whenever(storyBackend).search(any())
    }

    @Test
    fun `sitemap content`() {
        val sitemap = JAXB.unmarshal(URL("http://localhost:$port/sitemap.xml"), SitemapModel::class.java)

        assertHasUrl("/", sitemap)
        assertHasUrl("/about", sitemap)
        assertHasUrl("/writers", sitemap)
        assertHasUrl("/@/ray.sponsible", sitemap)
        assertHasUrl("/@/roger.milla", sitemap)
        assertHasUrl("/@/samuel.etoo", sitemap)
        assertHasUrl("/read/43800/this-is-a-problem", sitemap)
        assertHasUrl("/read/12342/roger-milla-marque-10-buts", sitemap)
    }

//    @Test
//    fun `sitemap header`() {
//        driver.get(url)
//
//        assertElementAttributeEndsWith("head link[rel='sitemap']", "href", "/sitemap.xml")
//    }

    private fun assertHasUrl(url: String, sitemap: SitemapModel) {
        assertNotNull(sitemap.url.find { it.loc.endsWith(url) })
    }
}
