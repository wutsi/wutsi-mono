package com.wutsi.blog.channel.service

import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.story.service.StoryService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ChannelListener(
    private val registry: ChannelRegistry,
    private val stories: StoryService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ChannelListener::class.java)
    }

    @EventListener
    @Async
    fun onPublish(event: PublishEvent) {
        LOGGER.info("onPublish $event")

        try {
            val story = stories.findById(event.storyId)
            if (story.publishToSocialMedia == true) {
                registry.publishStory(story)
            }
        } catch (ex: Exception) {
            LOGGER.error("Unexpected error while handling $event", ex)
        }
    }
}
