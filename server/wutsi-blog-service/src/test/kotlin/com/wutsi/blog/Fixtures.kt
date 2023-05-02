package com.wutsi.blog

import com.wutsi.blog.account.domain.User
import com.wutsi.blog.channel.domain.Channel
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.telegram.CheckBotAccessRequest
import com.wutsi.blog.client.telegram.TelegramChatType
import com.wutsi.blog.client.telegram.TelegramChatType.group
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

    fun createCheckBotAccessRequest(
        username: String = "ray.sponsible",
        chatType: TelegramChatType = group,
        chatTitle: String = "grp",
    ) = CheckBotAccessRequest(
        username = username,
        chatType = chatType,
        chatTitle = chatTitle,
    )

//    fun createTelegramUpdate(message: TelegramMessage? = null, post: TelegramMessage? = null) = TelegramUpdate(
//        update_id = System.currentTimeMillis(),
//        message = message,
//        channel_post = post
//    )
//
//    fun createTelegramMessage(chat: TelegramChat = TelegramChat()) = TelegramMessage(
//        message_id = System.currentTimeMillis(),
//        chat = chat
//    )
//
//    fun createTelegramChat(id: String, title: String, type: TelegramChatType) = TelegramChat(
//        id = id,
//        title = title,
//        type = type.name,
//        all_members_are_administrators = type == group
//    )
//
//    fun createTelegramUser(username: String, bot: Boolean) = TelegramUser(
//        username = username,
//        is_bot = bot
//    )
//
//    fun createTelegramChatMember(user: TelegramUser, status: String, canPost: Boolean) = TelegramChatMember(
//        user = user,
//        status = status,
//        can_post_messages = canPost
//    )
//
//    fun createTelegramGetChatAdministratorsResponse(members: List<TelegramChatMember>) =
//        TelegramGetChatAdministratorsResponse(
//            ok = true,
//            result = members
//        )
//
//    fun createTelegramGetUpdatesResponse(updates: List<TelegramUpdate>) = TelegramGetUpdatesResponse(
//        ok = true,
//        result = updates
//    )
}
