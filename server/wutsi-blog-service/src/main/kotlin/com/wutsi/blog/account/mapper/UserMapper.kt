package com.wutsi.blog.account.mapper

import com.wutsi.blog.account.domain.User
import com.wutsi.blog.client.user.UserDto
import com.wutsi.blog.client.user.UserSummaryDto
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUserDto(user: User) = UserDto(
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
        storyCount = user.storyCount,
        followerCount = user.followerCount,
        subscriberCount = user.subscriberCount,
        lastPublicationDateTime = user.lastPublicationDateTime,
        testUser = user.testUser,
    )

    fun toUserSummaryDto(user: User) = UserSummaryDto(
        id = user.id!!,
        name = user.name,
        fullName = user.fullName,
        email = user.email,
        pictureUrl = user.pictureUrl,
        blog = user.blog,
        biography = user.biography,
        storyCount = user.storyCount,
        followerCount = user.followerCount,
        subscriberCount = user.subscriberCount,
        testUser = user.testUser,
    )
}
