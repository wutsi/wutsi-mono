package com.wutsi.blog.user.service.recommendation

import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.service.UserRecommenderStrategy
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SimilarityModelType
import org.springframework.stereotype.Service

@Service
class UserRecommenderMLStrategy(
    private val subscriptionService: SubscriptionService,
    private val similarityBackend: SimilarityBackend,
    private val readerService: ReaderService,
    private val storyService: StoryService,
) : UserRecommenderStrategy {
    override fun recommend(request: RecommendUserRequest): List<Long> {
        // get the story ids
        var userIds = if (request.readerId != null) {
            subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = request.readerId!!,
                    limit = request.limit,
                ),
            ).mapNotNull { it.userId }
        } else {
            emptyList()
        }

        if (userIds.isEmpty()) {
            val storyIds = readerService.findViewedStoryIds(request.readerId, request.deviceId)
            if (storyIds.isEmpty()) {
                return emptyList()
            }

            // get the story authors
            userIds = storyService.searchStories(
                SearchStoryRequest(
                    storyIds = storyIds,
                    limit = storyIds.size,
                ),
            ).map { it.userId }
        }

        // Return similar authors
        return similarityBackend.search(
            SearchSimilarityRequest(
                itemIds = userIds,
                limit = request.limit,
                model = SimilarityModelType.AUTHOR_TIFDF,
            ),
        ).items.map { it.id }
    }
}
