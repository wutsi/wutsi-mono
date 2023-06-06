package com.wutsi.blog.story.mapper

import com.wutsi.blog.comment.dto.CommentCounter
import com.wutsi.blog.like.dto.LikeCounter
import com.wutsi.blog.pin.domain.PinStoryEntity
import com.wutsi.blog.share.dto.ShareCounter
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.domain.TopicEntity
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.util.SlugGenerator
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class StoryMapper(
    private val tagMapper: TagMapper,
    private val topicMapper: TopicMapper,
) {
    fun toStoryDto(
        story: StoryEntity,
        content: Optional<StoryContentEntity>,
        topic: TopicEntity?,
        pin: PinStoryEntity? = null,
        like: LikeCounter? = null,
        comment: CommentCounter? = null,
        share: ShareCounter? = null,
    ) = Story(
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
        scheduledPublishDateTime = story.scheduledPublishDateTime,
        access = story.access,
        pinned = pin?.storyId == story.id,
        totalLikes = like?.count ?: 0L,
        liked = like?.liked == true,
        totalComments = comment?.count ?: 0L,
        commented = comment?.commented == true,
        totalShares = share?.count ?: 0L,
    )

    fun toStorySummaryDto(
        story: StoryEntity,
        pin: PinStoryEntity? = null,
        like: LikeCounter? = null,
        comment: CommentCounter? = null,
        share: ShareCounter? = null,
    ) = StorySummary(
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
        scheduledPublishDateTime = story.scheduledPublishDateTime,
        access = story.access,
        pinned = pin?.storyId == story.id,
        totalLikes = like?.count ?: 0L,
        liked = like?.liked == true,
        totalComments = comment?.count ?: 0L,
        commented = comment?.commented == true,
        totalShares = share?.count ?: 0L,
    )

    fun slug(story: StoryEntity, language: String? = null): String {
        val slug = SlugGenerator.generate("/read/${story.id}", story.title)
        return if (language == null || story.language == language) slug else "$slug?translate=$language"
    }
}
