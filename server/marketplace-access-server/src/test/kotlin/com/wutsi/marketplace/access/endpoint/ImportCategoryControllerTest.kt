package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.CategoryRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
public class ImportCategoryControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: CategoryRepository

    private val rest = RestTemplate()

    @Test
    public fun invoke() {
        rest.postForEntity(url(), null, Any::class.java)
        Thread.sleep(30000L)

        val categories = dao.findAll()
        assertEquals(5595, categories.toList().size)

        val category = dao.findById(503026L).get()
        assertEquals(503026L, category.id)
        assertEquals("Mature > Weapons > Gun Care & Accessories > Reloading Supplies & Equipment", category.longTitle)
        assertEquals("Reloading Supplies & Equipment", category.title)
        assertEquals(
            "Adulte > Armes > Accessoires et entretien des armes à feu > Accessoires et équipements pour le rechargement",
            category.longTitleFrench,
        )
        assertEquals("Accessoires et équipements pour le rechargement", category.titleFrench)
        assertEquals(3, category.level)
    }

    private fun url() = "http://localhost:$port/v1/categories/import"
}
