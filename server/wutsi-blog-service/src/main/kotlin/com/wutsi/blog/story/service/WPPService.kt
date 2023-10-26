package com.wutsi.blog.story.service

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.story.dto.WPPValidation
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
class WPPService(
    private val userService: UserService,

    ) {
    fun validate(story: StoryEntity): WPPValidation {
        val user = userService.findById(story.userId)

        return WPPValidation(
            readabilityRule = story.readabilityScore >= WPPConfig.MIN_READABILITY,
            thumbnailRule = !story.thumbnailUrl.isNullOrEmpty(),
            wordCountRule = story.wordCount >= WPPConfig.MIN_WORD_COUNT,
            subscriptionRule = user.subscriberCount >= WPPConfig.MIN_SUBSCRIBER_COUNT,
            storyCountRule = user.publishStoryCount >= WPPConfig.MIN_STORY_COUNT,
            blogAgeRule = DateUtils.addMonths(user.creationDateTime, WPPConfig.MIN_AGE_MONTHS) < Date()
        )
    }
}
