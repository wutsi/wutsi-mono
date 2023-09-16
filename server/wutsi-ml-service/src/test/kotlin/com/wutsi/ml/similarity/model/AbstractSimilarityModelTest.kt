package com.wutsi.ml.similarity.model

import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractSimilarityModelTest {
    @Autowired
    private lateinit var storage: StorageService

    protected abstract fun getModel(): SimilarityModel

    @BeforeEach
    fun setUp() {
        val model = getModel()
        storage.store(
            path = model.getEmbeddingModel().getNNIndexPath(),
            content = ByteArrayInputStream(
                """
                    100,200,300,400
                    1.0,0.1,0.2,0.0
                    0.1,1.0,0.9,0.1
                    0.2,0.9,1.0,0.3
                    0.0,0.1,0.3,1.0
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )
        model.reload()
    }

    @Test
    fun search() {
        // WHEN
        val request = SearchSimilarityRequest(
            itemIds = listOf(300L),
        )
        val response = getModel().search(request)

        // THEN
        val items = response.items
        assertEquals(3, items.size)

        assertEquals(200L, items[0].id)
        assertEquals(0.9, items[0].score)

        assertEquals(400L, items[1].id)
        assertEquals(0.3, items[1].score)

        assertEquals(100L, items[2].id)
        assertEquals(0.2, items[2].score)
    }

    @Test
    fun searchMultipleStories() {
        // WHEN
        val request = SearchSimilarityRequest(
            itemIds = listOf(100L, 200L),
        )
        val response = getModel().search(request)

        // THEN
        val items = response.items
        assertEquals(2, items.size)

        assertEquals(300L, items[0].id)
        assertEquals(0.9, items[0].score)

        assertEquals(400L, items[1].id)
        assertEquals(0.1, items[1].score)
    }

    @Test
    fun badId() {
        // WHEN
        val request = SearchSimilarityRequest(
            itemIds = listOf(99999L),
        )
        val response = getModel().search(request)

        // THEN
        assertEquals(0, response.items.size)
    }
}
