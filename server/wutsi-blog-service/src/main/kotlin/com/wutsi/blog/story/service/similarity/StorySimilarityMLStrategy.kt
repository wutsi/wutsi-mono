package com.wutsi.blog.story.service.similarity

import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.service.StorySimilarityStrategy
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SimilarityModelType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StorySimilarityMLStrategy(
    private val similarityBackend: SimilarityBackend,
) : StorySimilarityStrategy {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StorySimilarityMLStrategy::class.java)
    }

    override fun search(request: SearchSimilarStoryRequest): List<Long> =
        try {
            similarityBackend.search(
                SearchSimilarityRequest(
                    itemIds = request.storyIds,
                    limit = request.limit,
                    model = SimilarityModelType.STORY_TIFDF,
                ),
            ).items.map { it.id }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve similar items", ex)
            emptyList()
        }
}
