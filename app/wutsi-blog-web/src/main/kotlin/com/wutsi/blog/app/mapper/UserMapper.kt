package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.client.user.UserDto
import com.wutsi.blog.client.user.UserSummaryDto
import com.wutsi.blog.subscription.dto.SubscriptionCounter
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper {
    fun toUserModel(user: UserDto, subscriptions: List<SubscriptionCounter>): UserModel {
        val subscriptionByUserId = subscriptions.associateBy { it.userId }

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
            storyCount = user.storyCount,
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
            subscriberCount = subscriptionByUserId[user.id]?.count ?: 0,
            subscriberCountText = subscriptionByUserId[user.id]?.count?.let { NumberUtils.toHumanReadable(it) } ?: "",
            subscribed = subscriptionByUserId[user.id]?.subscribed ?: false,
        )
    }

    fun slug(user: UserDto) = "/@/${user.name}"

    fun slug(user: UserSummaryDto) = "/@/${user.name}"

    fun toUserModel(user: UserSummaryDto, subscriptions: List<SubscriptionCounter>): UserModel {
        val subscriptionByUserId = subscriptions.associateBy { it.userId }
        return UserModel(
            id = user.id,
            name = user.name,
            fullName = user.fullName,
            pictureUrl = user.pictureUrl,
            pictureSmallUrl = user.pictureUrl, // imageKit.transform(user.pictureUrl, pictureSmallWidth.toString(), autoFocus = true)
            slug = slug(user),
            biography = user.biography,
            storyCount = user.storyCount,
            subscriberCount = subscriptionByUserId[user.id]?.count ?: 0,
            subscriberCountText = subscriptionByUserId[user.id]?.count?.let { NumberUtils.toHumanReadable(it) } ?: "",
            subscribed = subscriptionByUserId[user.id]?.subscribed ?: false,
            testUser = user.testUser,
        )
    }
}
