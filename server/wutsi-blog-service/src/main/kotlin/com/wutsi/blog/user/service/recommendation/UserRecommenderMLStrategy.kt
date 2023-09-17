package com.wutsi.blog.user.service.recommendation

import com.wutsi.blog.backend.SimilarityBackend
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus
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
    private val likeService: LikeService,
    private val readerService: ReaderService,
    private val similarityBackend: SimilarityBackend,
    private val storyService: StoryService,
    private val logger: KVLogger,
) : UserRecommenderStrategy {
    companion object {
        val LIMIT = 100
    }

    /**
     * Get liked OR recently read blogs, excluding subscribed blogs
     * Supplement with similar blogs, excluding subscribed blogs
     */
    override fun recommend(request: RecommendUserRequest): List<Long> {
        // Liked stories
        val storyIds = getLikedStoryIds(request).toMutableList()
        log("story_liked", storyIds, request)

        // Recently read stories
        if (storyIds.isEmpty()) {
            val readIds = getRecentlyReadStoryIds(request, storyIds).toMutableList()
            log("story_read", readIds, request)
            storyIds.addAll(readIds)
        }

        // No stories found
        if (storyIds.isEmpty()) {
            return emptyList()
        }

        // subscribed blog
        val subscribedId = getSubscribedBlogIds(request).toMutableList()
        log("user_subscribed", subscribedId, request)

        // Blogs
        val userIds = getStoryBlogs(request, storyIds, subscribedId).toMutableList()
        log("user", userIds, request)

        // Supplement with similar blogs
        if (userIds.size < request.limit) {
            val similarIds = getSimilarReadBlogIds(request, userIds, subscribedId)
            log("user_similar", similarIds, request)

            userIds.addAll(similarIds)
        }
        return userIds.take(request.limit)
    }

    private fun log(prefix: String, items: Collection<*>, request: RecommendUserRequest) {
        logger.add("${prefix}_ids", items.take(request.limit))
        logger.add("${prefix}_size", items.size)
    }

    private fun getLikedStoryIds(request: RecommendUserRequest): List<Long> =
        likeService.findByUserIdOrDeviceId(request.readerId, request.deviceId, LIMIT)
            .map { it.storyId }

    private fun getRecentlyReadStoryIds(request: RecommendUserRequest, storyIds: List<Long>): Collection<Long> =
        readerService.findViewedStoryIds(request.readerId, request.deviceId)
            .filter { !storyIds.contains(it) }

    private fun getSubscribedBlogIds(request: RecommendUserRequest): List<Long> =
        if (request.readerId == null) {
            emptyList()
        } else {
            subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = request.readerId!!,
                    limit = LIMIT,
                ),
            ).map { it.userId }.toMutableList()
        }

    private fun getStoryBlogs(
        request: RecommendUserRequest,
        storyIds: List<Long>,
        subsbscribedBlogIds: List<Long>,
    ): Collection<Long> =
        storyService.searchStories(
            SearchStoryRequest(
                storyIds = storyIds,
                limit = storyIds.size,
                status = StoryStatus.PUBLISHED,
            ),
        ).map { it.userId }
            .filter { !subsbscribedBlogIds.contains(it) && it != request.readerId }
            .toSet()

    private fun getSimilarReadBlogIds(
        request: RecommendUserRequest,
        userIds: List<Long>,
        subsbscribedBlogIds: List<Long>,
    ): Collection<Long> =
        similarityBackend.search(
            SearchSimilarityRequest(
                itemIds = userIds.toList(),
                limit = LIMIT,
                model = SimilarityModelType.AUTHOR_TIFDF,
            ),
        ).items.map { it.id }
            .filter { !userIds.contains(it) && it != request.readerId && !subsbscribedBlogIds.contains(it) }
}
