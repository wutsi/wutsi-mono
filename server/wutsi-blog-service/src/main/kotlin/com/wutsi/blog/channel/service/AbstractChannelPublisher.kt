package com.wutsi.blog.channel.service

import com.wutsi.blog.channel.domain.SocialPost
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.messaging.UrlShortener
import org.slf4j.LoggerFactory

abstract class AbstractChannelPublisher(
    protected val stories: StoryService,
    protected val urlShortener: UrlShortener,
) : ChannelPublisher {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AbstractChannelPublisher::class.java)
    }

    override fun publishStory(story: StoryEntity) {
        publishPost(
            SocialPost(
                story = story,
                message = story.socialMediaMessage,
                pictureUrl = null,
                campaign = null,
            ),
        )
    }

    override fun publishPost(socialPost: SocialPost): String =
        publish(socialPost)

    abstract fun publish(post: SocialPost): String

    protected fun generateText(post: SocialPost): String =
        post.message ?: ""

    protected fun generateUrl(post: SocialPost): String {
        val url = StringBuilder()
        url.append(stories.url(story = post.story, language = post.language))
        if (!url.contains('?')) {
            url.append('?')
        } else {
            url.append('&')
        }
        url.append("utm_source=" + type().name)
        if (post.campaign != null) {
            url.append("&utm_campaign=${post.campaign}")
        }

        try {
            return urlShortener.shorten(url.toString())
        } catch (ex: Exception) {
            LOGGER.warn("Unable to shorten $url", ex)
            return url.toString()
        }
    }
}
