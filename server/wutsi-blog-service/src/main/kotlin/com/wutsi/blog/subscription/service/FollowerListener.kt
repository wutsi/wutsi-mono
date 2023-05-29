package com.wutsi.blog.subscription.service

import com.wutsi.blog.client.event.FollowEvent
import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.client.event.UnfollowEvent
import com.wutsi.blog.client.event.UpdateUserEvent
import com.wutsi.blog.story.service.StoryService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Deprecated("")
@Service
class FollowerListener(
    private val followerService: FollowerService,
    private val storyService: StoryService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FollowerListener::class.java)
    }

    @Async
    @EventListener
    fun onFollow(event: FollowEvent) {
        LOGGER.info("onFollow $event")
        followerService.updateFollowerCount(event.userId)
    }

    @Async
    @EventListener
    fun onUnFollow(event: UnfollowEvent) {
        LOGGER.info("onUnfollow $event")
        followerService.updateFollowerCount(event.userId)
    }

    @Async
    @EventListener
    fun onUserUpdated(event: UpdateUserEvent) {
        LOGGER.info("onUserUpdated $event")

        if (event.name == "blog" && event.value == "true") {
            followerService.autoFollow(event.userId)
        }
    }

    @Async
    @EventListener
    fun onPublish(event: PublishEvent) {
        LOGGER.info("onPublish $event")

        val story = storyService.findById(event.storyId)
        followerService.autoFollow(story.userId)
    }
}
