package com.wutsi.ml.similarity.model

import com.wutsi.ml.similarity.dto.SimilarityModelType
import com.wutsi.ml.similarity.model.author.AuthorTfidfSimilarityModel
import com.wutsi.ml.similarity.model.story.StoryTfidfSimilarityModel
import org.springframework.stereotype.Service

@Service
class SimilarityModelFactory(
    private val storyTfidfSimilarityModel: StoryTfidfSimilarityModel,
    private val authorTfidfSimilarityModel: AuthorTfidfSimilarityModel,
) {
    fun get(type: SimilarityModelType): SimilarityModel =
        when (type) {
            SimilarityModelType.STORY_TIFDF -> storyTfidfSimilarityModel
            SimilarityModelType.AUTHOR_TIFDF -> authorTfidfSimilarityModel
            else -> throw IllegalStateException("Not supported: $type")
        }
}
