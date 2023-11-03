package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper(
    private val imageKit: ImageService,
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) {
    fun toUserModel(user: User, runAs: Boolean = false): UserModel {
        return UserModel(
            id = user.id,
            name = user.name,
            biography = user.biography,
            fullName = user.fullName,
            pictureUrl = user.pictureUrl,
            pictureSmallUrl = toPictureUrl(user.pictureUrl),
            websiteUrl = user.websiteUrl?.ifEmpty { null },
            email = user.email,
            slug = slug(user),
            facebookUrl = user.facebookId?.ifEmpty { null }?.let { "https://www.facebook.com/$it" },
            linkedinUrl = user.linkedinId?.ifEmpty { null }?.let { "https:/gi/www.linkedin.com/in/$it" },
            twitterUrl = user.twitterId?.ifEmpty { null }?.let { "https://www.twitter.com/$it" },
            youtubeUrl = user.youtubeId?.ifEmpty { null }?.let { "https://www.youtube.com/$it" },
            telegramUrl = user.telegramId?.ifEmpty { null }?.let { "https://t.me/$it" },
            whatsappUrl = user.whatsappId?.ifEmpty { null }?.let { "https://wa.me/$it" },
            messengerUrl = user.facebookId?.ifEmpty { null }?.let { "https://m.me/$it" },
            githubUrl = user.githubId?.ifEmpty { null }?.let { "https://www.github.com/$it" },
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
            githubId = user.githubId,
            hasInstantMessagingLinks = !user.telegramId.isNullOrEmpty() || !user.whatsappId.isNullOrEmpty(),
            hasSocialLinks = !user.facebookId.isNullOrEmpty() ||
                !user.youtubeId.isNullOrEmpty() ||
                !user.linkedinId.isNullOrEmpty() ||
                !user.twitterId.isNullOrEmpty(),
            testUser = user.testUser,
            subscribed = user.subscribed,
            subscriberCount = user.subscriberCount,
            storyCount = user.storyCount,
            publishStoryCount = user.publishStoryCount,
            draftStoryCount = user.draftStoryCount,
            pinStoryId = user.pinStoryId,
            readCount = user.readCount,
            walletId = user.walletId,
            country = user.country,
            url = serverUrl + slug(user),
            aboutUrl = serverUrl + slug(user) + "/about",
            donationUrl = if (user.walletId != null) {
                serverUrl + slug(user) + "/donate"
            } else {
                null
            },
            totalDurationSeconds = user.totalDurationSeconds,
            wpp = user.wpp,
            creationDateTime = user.creationDateTime,
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
            pictureSmallUrl = toPictureUrl(user.pictureUrl),
            slug = slug(user),
            biography = user.biography,
            testUser = user.testUser,
            subscribed = user.subscribed,
            subscriberCount = user.subscriberCount,
            storyCount = user.storyCount,
            publishStoryCount = user.publishStoryCount,
            draftStoryCount = user.draftStoryCount,
            readCount = user.readCount,
            blog = user.blog,
            url = serverUrl + slug(user),
            email = user.email,
            totalDurationSeconds = user.totalDurationSeconds,
            wpp = user.wpp,
            creationDateTime = user.creationDateTime,
            language = user.language,
        )
    }

    private fun toPictureUrl(pictureUrl: String?): String? =
        pictureUrl?.let {
            imageKit.transform(
                url = it,
                transformation = Transformation(
                    Dimension(width = 256),
                    focus = Focus.FACE,
                ),
            )
        }
}
