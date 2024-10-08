package com.wutsi.blog.story.mapper

import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.like.domain.LikeEntity
import com.wutsi.blog.product.domain.CategoryEntity
import com.wutsi.blog.product.mapper.CategoryMapper
import com.wutsi.blog.share.domain.ShareEntity
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.domain.TopicEntity
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.util.StringUtils
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class StoryMapper(
    private val tagMapper: TagMapper,
    private val topicMapper: TopicMapper,
    private val categoryMapper: CategoryMapper,
) {
    fun toStoryDto(
        story: StoryEntity,
        content: Optional<StoryContentEntity>,
        topic: TopicEntity?,
        user: UserEntity? = null,
        like: LikeEntity? = null,
        comment: CommentEntity? = null,
        share: ShareEntity? = null,
        category: CategoryEntity? = null,
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
        topic = topic?.let { topicMapper.toTopicDto(it) },
        scheduledPublishDateTime = story.scheduledPublishDateTime,
        access = story.access,
        pinned = user?.pinStoryId == story.id,
        likeCount = story.likeCount,
        liked = like != null,
        commentCount = story.commentCount,
        commented = comment != null,
        shareCount = story.shareCount,
        shared = share != null,
        readCount = story.readCount,
        video = story.video ?: false,
        wppScore = story.wppScore,
        subscriberReaderCount = story.subscriberReaderCount,
        attachmentDownloadCount = story.attachmentDownloadCount,
        recipientCount = story.recipientCount,
        totalDurationSeconds = story.totalDurationSeconds,
        clickCount = story.clickCount,
        readerCount = story.readerCount,
        emailReaderCount = story.emailReaderCount,
        subscriberCount = story.subscriberCount,
        contentModificationDateTime = story.contentModificationDateTime,
        category = category?.let { categoryMapper.toCategory(category) },
        productId = story.productId,
    )

    fun toStorySummaryDto(
        story: StoryEntity,
        user: UserEntity? = null,
        like: LikeEntity? = null,
        comment: CommentEntity? = null,
        share: ShareEntity? = null,
    ) = StorySummary(
        id = story.id!!,
        userId = story.userId,
        status = story.status,
        creationDateTime = story.creationDateTime,
        modificationDateTime = story.modificationDateTime,
        publishedDateTime = story.publishedDateTime,
        summary = story.summary,
        title = story.title,
        language = story.language,
        readingMinutes = story.readingMinutes,
        thumbnailUrl = story.thumbnailUrl,
        wordCount = story.wordCount,
        slug = slug(story),
        topicId = story.topicId,
        scheduledPublishDateTime = story.scheduledPublishDateTime,
        access = story.access,
        pinned = user?.pinStoryId == story.id,
        likeCount = story.likeCount,
        liked = like != null,
        commentCount = story.commentCount,
        commented = comment != null,
        shareCount = story.shareCount,
        shared = share != null,
        readCount = story.readCount,
        video = story.video ?: false,
        totalDurationSeconds = story.totalDurationSeconds,
        subscriberReaderCount = story.subscriberReaderCount,
        recipientCount = story.recipientCount,
        clickCount = story.clickCount,
        readerCount = story.readerCount,
        emailReaderCount = story.emailReaderCount,
        contentModificationDateTime = story.contentModificationDateTime,
        categoryId = story.categoryId,
        productId = story.productId,
    )

    fun slug(story: StoryEntity, language: String? = null): String {
        val slug = StringUtils.toSlug("/read/${story.id}", story.title)
        return if (language == null || story.language == language) slug else "$slug?translate=$language"
    }
}
