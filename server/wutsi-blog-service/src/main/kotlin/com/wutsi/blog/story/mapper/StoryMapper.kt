package com.wutsi.blog.story.mapper

import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.domain.StoryContent
import com.wutsi.blog.story.domain.Topic
import com.wutsi.blog.util.SlugGenerator
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class StoryMapper(
    private val tagMapper: TagMapper,
    private val topicMapper: TopicMapper,
) {
    fun toStoryDto(story: Story, content: Optional<StoryContent>, topic: Topic?) = StoryDto(
        id = story.id!!,
        userId = story.userId,
        status = story.status,
        creationDateTime = story.creationDateTime,
        modificationDateTime = story.modificationDateTime,
        publishedDateTime = story.publishedDateTime,
        summary = content.map { it.summary }.orElse(story.summary),
        tagline = content.map { it.tagline }.orElse(story.tagline),
        title = content.map { it.title }.orElse(story.title),
        content = content.map { it.content }.orElse(null),
        contentType = content.map { it.contentType }.orElse(null),
        language = content.map { it.language }.orElse(story.language),
        readingMinutes = story.readingMinutes,
        sourceUrl = story.sourceUrl,
        sourceSite = story.sourceSite,
        thumbnailUrl = story.thumbnailUrl,
        wordCount = story.wordCount,
        tags = story.tags.map { tagMapper.toTagDto(it) },
        slug = slug(story, content.map { it.language }.orElse(null)),
        readabilityScore = content.map { story.readabilityScore }.orElse(0),
        topic = topic?.let { topicMapper.toTopicDto(it) } ?: null,
        live = story.live,
        liveDateTime = story.liveDateTime,
        wppStatus = story.wppStatus,
        wppRejectionReason = story.wppRejectionReason,
        wppModificationDateTime = story.wppModificationDateTime,
        socialMediaMessage = story.socialMediaMessage,
        scheduledPublishDateTime = story.scheduledPublishDateTime,
        publishToSocialMedia = story.publishToSocialMedia == true,
        access = story.access,
        siteId = story.siteId,
    )

    fun toStorySummaryDto(story: Story) = StorySummaryDto(
        id = story.id!!,
        userId = story.userId,
        status = story.status,
        creationDateTime = story.creationDateTime,
        modificationDateTime = story.modificationDateTime,
        publishedDateTime = story.publishedDateTime,
        summary = story.summary,
        tagline = story.tagline,
        title = story.title,
        language = story.language,
        readingMinutes = story.readingMinutes,
        sourceUrl = story.sourceUrl,
        thumbnailUrl = story.thumbnailUrl,
        wordCount = story.wordCount,
        slug = slug(story),
        topicId = story.topicId,
        live = story.live,
        liveDateTime = story.liveDateTime,
        wppStatus = story.wppStatus,
        wppRejectionReason = story.wppRejectionReason,
        wppModificationDateTime = story.wppModificationDateTime,
        scheduledPublishDateTime = story.scheduledPublishDateTime,
        access = story.access,
        siteId = story.siteId,
    )

    fun slug(story: Story, language: String? = null): String {
        var slug = SlugGenerator.generate("/read/${story.id}", story.title)
        return if (language == null || story.language == language) slug else "$slug?translate=$language"
    }
}
