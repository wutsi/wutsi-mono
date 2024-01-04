package com.wutsi.blog.story.service.recommendation

import com.wutsi.blog.backend.PersonalizeBackend
import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.blog.story.service.StoryRecommenderStrategy
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SimilarityModelType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Deprecated("ML disabled for the moment")
@Service
class StoryRecommenderMLStrategy(
    private val similarityBackend: SimilarityBackend,
    private val personalizeBackend: PersonalizeBackend,
    private val readerService: ReaderService,
) : StoryRecommenderStrategy {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryRecommenderMLStrategy::class.java)
    }

    override fun recommend(request: RecommendStoryRequest): List<Long> {
        try {
            if (request.readerId != null) {
                // Recommend stories for the reader
                return personalizeBackend.recommend(
                    com.wutsi.ml.personalize.dto.RecommendStoryRequest(
                        userId = request.readerId!!,
                        limit = request.limit,
                    ),
                ).stories.map { it.id }
            } else {
                // Get the recent stories read by user
                val storyIds = readerService.findViewedStoryIds(null, request.deviceId)
                if (storyIds.isEmpty()) {
                    return emptyList()
                }

                // Similar stories
                return similarityBackend.search(
                    SearchSimilarityRequest(
                        itemIds = storyIds,
                        limit = request.limit,
                        model = SimilarityModelType.STORY_TIFDF,
                    ),
                ).items.map { it.id }
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve recommendations", ex)
            return emptyList()
        }
    }
}
