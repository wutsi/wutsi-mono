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
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class UserRecommenderMLStrategy(
    private val subscriptionService: SubscriptionService,
    private val similarityBackend: SimilarityBackend,
    private val readerService: ReaderService,
    private val storyService: StoryService,
    private val logger: KVLogger,
) : UserRecommenderStrategy {
    override fun recommend(request: RecommendUserRequest): List<Long> {
        // get subscribed blog
        val userIds: MutableList<Long> = if (request.readerId != null) {
            subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = request.readerId!!,
                    limit = request.limit,
                ),
            ).map { it.userId }.toMutableList()
        } else {
            emptyList<Long>().toMutableList()
        }
        logger.add("blog_subscribed_id", userIds)

        // Add recently read blogs
        if (userIds.size < request.limit) {
            val storyIds = readerService.findViewedStoryIds(request.readerId, request.deviceId)
            if (storyIds.isEmpty()) {
                return emptyList()
            }

            // get the story authors
            val readIds = storyService.searchStories(
                SearchStoryRequest(
                    storyIds = storyIds,
                    limit = storyIds.size,
                ),
            ).map { it.userId }
                .filter { !userIds.contains(it) }
                .toSet()
            logger.add("blog_read_id", readIds)

            userIds.addAll(readIds)
        }

        // Add similar users
        if (userIds.size < request.limit) {
            val similarIds = similarityBackend.search(
                SearchSimilarityRequest(
                    itemIds = userIds.toList(),
                    limit = request.limit,
                    model = SimilarityModelType.AUTHOR_TIFDF,
                ),
            ).items.map { it.id }
                .filter { !userIds.contains(it) }

            logger.add("blog_similar_id", similarIds)

            userIds.addAll(similarIds)
        }
        return userIds.take(request.limit)
    }
}
