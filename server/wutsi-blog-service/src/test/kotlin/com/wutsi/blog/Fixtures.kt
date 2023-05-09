package com.wutsi.blog

import com.wutsi.blog.account.domain.User
import com.wutsi.blog.channel.domain.Channel
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.view.PreferredAuthorDto
import com.wutsi.blog.client.view.ViewDto
import com.wutsi.blog.story.domain.Story
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.UUID

object Fixtures {
    fun createMessageSource(): ResourceBundleMessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("messages")
        return messageSource
    }

    fun createStories(storyIds: List<Long>) = storyIds.map { createStory(it) }

    fun createStory(
        id: Long,
        title: String? = null,
        tagline: String? = null,
        user: User = createUser(id),
        socialMediaMessage: String? = null,
        publishToSocialMedia: Boolean = true,
        readingMinutes: Int = 1,
    ) = Story(
        id = id,
        title = title,
        tagline = tagline,
        status = StoryStatus.published,
        socialMediaMessage = socialMediaMessage,
        tags = emptyList(),
        userId = user.id!!,
        publishToSocialMedia = publishToSocialMedia,
        readingMinutes = readingMinutes,
    )

//    fun createComment(id: Long, text: String, user: User, story: Story) = Comment(
//        id = id,
//        userId = user.id?.let { it } ?: -1,
//        text = text,
//        storyId = story.id?.let { it } ?: -1
//    )

    fun createUser(
        id: Long,
        email: String = "user$id@gmail.com",
        pictureUrl: String = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg",
        siteId: Long = 1,
    ) = User(
        id = id,
        siteId = siteId,
        name = "user$id",
        email = email,
        fullName = "User Foo $id",
        pictureUrl = pictureUrl,
        language = "fr",
        facebookId = id.toString(),
        twitterId = id.toString(),
        youtubeId = id.toString(),
    )

    fun createPreferredAuthor(authorId: Long, score: Double) = PreferredAuthorDto(
        authorId = authorId,
        score = score,
    )

    fun createView(storyId: Long) = ViewDto(
        storyId = storyId,
    )

    fun createChannel(type: ChannelType) = Channel(
        id = System.currentTimeMillis(),
        type = type,
        providerUserId = "434903",
        userId = System.currentTimeMillis(),
        accessToken = UUID.randomUUID().toString(),
        name = "test",
        pictureUrl = null,
        accessTokenSecret = UUID.randomUUID().toString(),
    )
}
