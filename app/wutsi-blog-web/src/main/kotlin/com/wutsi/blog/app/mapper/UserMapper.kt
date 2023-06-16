package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper {
    fun toUserModel(user: User, runAs: Boolean = false): UserModel {
        return UserModel(
            id = user.id,
            name = user.name,
            biography = user.biography,
            fullName = user.fullName,
            pictureUrl = user.pictureUrl,
            pictureSmallUrl = user.pictureUrl, // imageKit.transform(user.pictureUrl, pictureSmallWidth.toString(), autoFocus = true)
            websiteUrl = user.websiteUrl,
            email = user.email,
            loginCount = user.loginCount,
            slug = slug(user),
            facebookUrl = user.facebookId?.let { "https://www.facebook.com/$it" },
            linkedinUrl = user.linkedinId?.let { "https://www.linkedin.com/in/$it" },
            twitterUrl = user.twitterId?.let { "https://www.twitter.com/$it" },
            youtubeUrl = user.youtubeId?.let { "https://www.youtube.com/channel/$it" },
            telegramUrl = user.telegramId?.let { "https://t.me/$it" },
            whatsappUrl = user.whatsappId?.let { "https://wa.me/$it" },
            messengerUrl = user.facebookId?.let { "https://m.me/$it" },
            rssUrl = slug(user) + "/rss",
            superUser = user.superUser,
            blog = user.blog,
            readAllLanguages = user.readAllLanguages,
            language = user.language,
            locale = if (user.language == null) null else Locale(user.language, "CM"),
            facebookId = user.facebookId,
            twitterId = user.twitterId,
            linkedinId = user.linkedinId,
            youtubeId = user.youtubeId,
            telegramId = user.telegramId,
            whatsappId = user.whatsappId,
            hasInstantMessagingLinks = !user.telegramId.isNullOrEmpty() ||
                !user.whatsappId.isNullOrEmpty(),
            hasSocialLinks = !user.facebookId.isNullOrEmpty() ||
                !user.youtubeId.isNullOrEmpty() ||
                !user.linkedinId.isNullOrEmpty() ||
                !user.twitterId.isNullOrEmpty(),
            testUser = user.testUser,
            subscribed = user.subscribed,
            subscriberCount = user.subscriberCount,
            subscriberCountText = NumberUtils.toHumanReadable(user.subscriberCount),
            storyCount = user.storyCount,
            publishStoryCount = user.publishStoryCount,
            draftStoryCount = user.draftStoryCount,
            pinStoryId = user.pinStoryId,
            readCount = user.readCount,
            readCountText = NumberUtils.toHumanReadable(user.readCount),
        )
    }

    fun slug(user: User) = "/@/${user.name}"

    fun slug(user: UserSummary) = "/@/${user.name}"

    fun toUserModel(user: UserSummary): UserModel {
        return UserModel(
            id = user.id,
            name = user.name,
            fullName = user.fullName,
            pictureUrl = user.pictureUrl,
            pictureSmallUrl = user.pictureUrl, // imageKit.transform(user.pictureUrl, pictureSmallWidth.toString(), autoFocus = true)
            slug = slug(user),
            biography = user.biography,
            testUser = user.testUser,
            subscribed = user.subscribed,
            subscriberCount = user.subscriberCount,
            subscriberCountText = NumberUtils.toHumanReadable(user.subscriberCount),
            storyCount = user.storyCount,
            publishStoryCount = user.publishStoryCount,
            draftStoryCount = user.draftStoryCount,
        )
    }
}
