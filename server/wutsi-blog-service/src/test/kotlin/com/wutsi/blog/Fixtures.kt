package com.wutsi.blog

import com.wutsi.blog.channel.domain.Channel
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.mail.service.Blog
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.user.domain.UserEntity
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
        user: UserEntity = createUser(id),
        socialMediaMessage: String? = null,
        publishToSocialMedia: Boolean = true,
        readingMinutes: Int = 1,
    ) = StoryEntity(
        id = id,
        title = title,
        tagline = tagline,
        status = StoryStatus.PUBLISHED,
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
    ) = UserEntity(
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

    fun createMailContext() = MailContext(
        assetUrl = "https://s3.amazonaws.com/int-wutsi",
        websiteUrl = "https://www.wutsi.com",
        template = "default",
        blog = Blog(
            logoUrl = "https://ik.imagekit.io/cx8qxsgz4d/user/12/picture/tr:w-64,h-64,fo-face/023bb5c8-7b09-4f2f-be51-29f5c851c2c0-scaled_image_picker1721723356188894418.png",
            name = "maison-h",
            fullName = "Maison K",
        ),
    )
}
