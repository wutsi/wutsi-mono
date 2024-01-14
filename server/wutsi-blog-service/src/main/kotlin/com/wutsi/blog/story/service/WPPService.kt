package com.wutsi.blog.story.service

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.story.dto.WPPValidation
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.math.min

@Service
class WPPService(private val userService: UserService) {
    companion object {
        const val RULE_COUNT = 6
        const val MIN_AGE_MILLIS = WPPConfig.MIN_AGE_MONTHS.toLong() * 30L * 96400L * 1000
    }

    fun validate(story: StoryEntity): WPPValidation {
        val user = userService.findById(story.userId)

        return WPPValidation(
            readabilityRule = story.readabilityScore >= WPPConfig.MIN_READABILITY,
            thumbnailRule = !story.thumbnailUrl.isNullOrEmpty(),
            wordCountRule = story.wordCount >= WPPConfig.MIN_WORD_COUNT,
            subscriptionRule = user.subscriberCount >= WPPConfig.MIN_SUBSCRIBER_COUNT,
            storyCountRule = user.publishStoryCount >= WPPConfig.MIN_STORY_COUNT,
            blogAgeRule = DateUtils.addMonths(user.creationDateTime, WPPConfig.MIN_AGE_MONTHS) < Date(),
            score = computeScore(story, user)
        )
    }

    private fun computeScore(story: StoryEntity, user: UserEntity): Int {
        val score: Double =
            min(1.0, story.readabilityScore.toDouble() / 100.0) +
                (story.thumbnailUrl?.ifEmpty { null }?.let { 1.0 } ?: 0.0) +
                min(1.0, story.wordCount.toDouble() / WPPConfig.MIN_WORD_COUNT.toDouble()) +
                min(1.0, user.subscriberCount.toDouble() / WPPConfig.MIN_SUBSCRIBER_COUNT.toDouble()) +
                min(1.0, user.publishStoryCount.toDouble() / WPPConfig.MIN_STORY_COUNT.toDouble()) +
                min(1.0, blogDate(user).time.toDouble() / MIN_AGE_MILLIS)

        return (100.0 * score / RULE_COUNT).toInt()
    }

    private fun blogDate(user: UserEntity): Date =
        user.blogDateTime ?: user.creationDateTime
}
