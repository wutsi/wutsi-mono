package com.wutsi.blog.app.page.reader

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RobotsControllerTest {
    @LocalServerPort
    protected val port: Int = 0

    @Test
    fun robot() {
        val txt = URL("http://localhost:$port/robots.txt").readText()
        assertEquals(
            """
                User-agent: *
                Disallow: /comments
                Disallow: /create
                Disallow: /login
                Disallow: /me/
                Disallow: /wclick
                
                User-agent: AhrefsBot
                Disallow: /
                User-agent: DotBot
                Disallow: /
                User-agent: MJ12bot
                Disallow: /
                User-agent: PetalBot
                Disallow: /
                User-agent: SemrushBot
                Disallow: /
                
                Sitemap: https://www.wutsi.com/sitemap.xml
            """.trimIndent(),
            txt
        )
    }
}