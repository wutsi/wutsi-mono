package com.wutsi.ml.personalize.service

import com.wutsi.ml.personalize.dto.RecommendStoryRequest
import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonalizeV1ServiceTest {
    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    protected lateinit var service: PersonalizeV1Service

    @BeforeEach
    fun setUp() {
        storage.store(
            path = PersonalizeV1.U_PATH,
            content = ByteArrayInputStream(
                """
                    11,0.7,0.1
                    22,0.1,0.2
                    33,1.0,0.9
                    44,0.9,1.0
                    55,0.1,0.3
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        storage.store(
            path = PersonalizeV1.V_PATH,
            content = ByteArrayInputStream(
                """
                    100,200,300,400
                    0.1,0.1,0.9,0.1
                    0.2,0.9,0.1,0.3
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )
    }

    @Test
    fun sort() {
        val request = SortStoryRequest(
            storyIds = listOf(400L, 100L, 300L, 200L, 500L),
            userId = 11L,
        )
        val response = service.sort(request)

        assertEquals(
            listOf(300L, 200L, 400L, 100L, 500L),
            response.map { it.first },
        )
    }

    @Test
    fun `sort invalid user-id`() {
        val request = SortStoryRequest(
            storyIds = listOf(100L, 200L, 300L, 400L, 500L),
            userId = 9999L,
        )
        val response = service.sort(request)

        assertEquals(
            listOf(100L, 200L, 300L, 400L, 500L),
            response.map { it.first },
        )
    }

    @Test
    fun recommend() {
        val request = RecommendStoryRequest(
            userId = 11L,
        )
        val response = service.recommend(request)

        assertEquals(
            listOf(300L, 200L, 400L, 100L),
            response.map { it.first },
        )
    }

    @Test
    fun `recommend invalid user-id`() {
        val request = RecommendStoryRequest(
            userId = 9999L,
        )
        val response = service.recommend(request)

        assertEquals(
            listOf<Long>(),
            response.map { it.first },
        )
    }
}
