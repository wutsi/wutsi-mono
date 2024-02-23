package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.model.SitemapModel
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import jakarta.xml.bind.JAXB
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.net.URL
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SitemapControllerTest : SeleniumTestSupport() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            SearchUserResponse(
                users = listOf(
                    UserSummary(id = 1, name = "ray.sponsible"),
                    UserSummary(id = 2, name = "roger.milla", storeId = "222"),
                    UserSummary(id = 3, name = "samuel.etoo", storeId = "333"),
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

        doReturn(
            SearchProductResponse(
                products = listOf(
                    ProductSummary(22210L, "Les amours perdus", slug = "/product/22210/les-amours-perdus"),
                    ProductSummary(22211L, "Les amours retrouves", slug = "/product/22211/les-amours-retrouves"),
                    ProductSummary(33310L, "Les 10 meilleurs repas", slug = "/product/33310/les-10-meilleurs-repas"),
                )
            )
        ).whenever(productBackend).search(any())
    }

    @Test
    fun `sitemap content`() {
        val sitemap = JAXB.unmarshal(URL("http://localhost:$port/sitemap.xml"), SitemapModel::class.java)

        assertHasUrl("/", sitemap)
        assertHasUrl("/about", sitemap)
        assertHasUrl("/writers", sitemap)
        assertHasUrl("/partner", sitemap)
        assertHasUrl("/@/ray.sponsible", sitemap)
        assertHasUrl("/@/roger.milla", sitemap)
        assertHasUrl("/@/samuel.etoo", sitemap)
        assertHasUrl("/read/43800/this-is-a-problem", sitemap)
        assertHasUrl("/read/12342/roger-milla-marque-10-buts", sitemap)
        assertHasUrl("/product/22210/les-amours-perdus", sitemap)
        assertHasUrl("/product/22211/les-amours-retrouves", sitemap)
        assertHasUrl("/product/33310/les-10-meilleurs-repas", sitemap)
    }

    @Test
    fun `sitemap in home header`() {
        driver.get(url)

        assertElementAttributeEndsWith("head link[rel='sitemap']", "href", "/sitemap.xml")
    }

    private fun assertHasUrl(url: String, sitemap: SitemapModel) {
        assertNotNull(sitemap.url.find { it.loc.endsWith(url) })
    }
}
