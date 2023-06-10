package com.wutsi.blog.account.mapper

import com.wutsi.blog.subscription.domain.SubscriptionEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUserDto(
        user: UserEntity,
        subscriptions: SubscriptionEntity? = null,
    ) = User(
        id = user.id!!,
        name = user.name,
        biography = user.biography,
        creationDateTime = user.creationDateTime,
        modificationDateTime = user.modificationDateTime,
        email = user.email,
        pictureUrl = user.pictureUrl,
        websiteUrl = user.websiteUrl,
        fullName = user.fullName,
        lastLoginDateTime = user.lastLoginDateTime,
        loginCount = user.loginCount,
        superUser = user.superUser,
        language = user.language,
        readAllLanguages = user.readAllLanguages,
        youtubeId = user.youtubeId,
        facebookId = user.facebookId,
        twitterId = user.twitterId,
        linkedinId = user.linkedinId,
        whatsappId = user.whatsappId,
        telegramId = user.telegramId,
        blog = user.blog,
        lastPublicationDateTime = user.lastPublicationDateTime,
        testUser = user.testUser,
        storyCount = user.storyCount,
        draftStoryCount = user.draftStoryCount,
        publishStoryCount = user.publishStoryCount,
        readCount = user.readCount,
        subscriberCount = user.subscriberCount,
        subscribed = subscriptions != null,
        pinStoryId = user.pinStoryId,
    )

    fun toUserSummaryDto(
        user: UserEntity,
        subscriptions: SubscriptionEntity? = null,
    ) = UserSummary(
        id = user.id!!,
        name = user.name,
        fullName = user.fullName,
        email = user.email,
        pictureUrl = user.pictureUrl,
        blog = user.blog,
        biography = user.biography,
        storyCount = user.storyCount,
        draftStoryCount = user.draftStoryCount,
        publishStoryCount = user.publishStoryCount,
        subscriberCount = user.subscriberCount,
        readCount = user.readCount,
        testUser = user.testUser,
        subscribed = subscriptions != null,
    )
}
