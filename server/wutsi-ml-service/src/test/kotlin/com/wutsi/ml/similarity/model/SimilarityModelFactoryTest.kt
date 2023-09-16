package com.wutsi.ml.similarity.model

import com.wutsi.ml.similarity.dto.SimilarityModelType
import com.wutsi.ml.similarity.model.author.AuthorTfidfSimilarityModel
import com.wutsi.ml.similarity.model.story.StoryTfidfSimilarityModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimilarityModelFactoryTest {
    @Autowired
    private lateinit var factory: SimilarityModelFactory

    @Test
    fun get() {
        assertEquals(StoryTfidfSimilarityModel::class.java, factory.get(SimilarityModelType.STORY_TIFDF)::class.java)
        assertEquals(AuthorTfidfSimilarityModel::class.java, factory.get(SimilarityModelType.AUTHOR_TIFDF)::class.java)
    }
}
