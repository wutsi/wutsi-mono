package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.TopicModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.editor.model.ReadabilityModel
import com.wutsi.blog.app.page.editor.model.ReadabilityRuleModel
import com.wutsi.blog.app.service.LocalizationService
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TopicService
import com.wutsi.blog.client.story.ReadabilityDto
import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.comment.dto.CommentCounter
import com.wutsi.blog.like.dto.LikeCounter
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Service
class StoryMapper(
    private val tagMapper: TagMapper,
    private val topicMapper: TopicMapper,
    private val topicService: TopicService,
    private val moment: Moment,
    private val htmlImageMapper: HtmlImageModelMapper,
    private val localizationService: LocalizationService,
    private val imageKit: ImageService,
    private val requestContext: RequestContext,

    @Value("\${wutsi.image.story.desktop.large.width}") private val desktopThumbnailLargeWidth: Int,
    @Value("\${wutsi.image.story.desktop.large.height}") private val desktopThumbnailLargeHeight: Int,
    @Value("\${wutsi.image.story.desktop.small.width}") private val desktopThumbnailSmallWidth: Int,
    @Value("\${wutsi.image.story.desktop.small.height}") private val desktopThumbnailSmallHeight: Int,

    @Value("\${wutsi.image.story.mobile.large.width}") private val mobileThumbnailLargeWidth: Int,
    @Value("\${wutsi.image.story.mobile.large.height}") private val mobileThumbnailLargeHeight: Int,
    @Value("\${wutsi.image.story.mobile.small.width}") private val mobileThumbnailSmallWidth: Int,
    @Value("\${wutsi.image.story.mobile.small.height}") private val mobileThumbnailSmallHeight: Int,

    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) {
    companion object {
        const val MAX_TAGS: Int = 5
    }

    fun toStoryModel(
        story: StoryDto,
        user: UserModel? = null,
        likes: List<LikeCounter>,
        comments: List<CommentCounter>,
        pinnedStoryId: Long? = null,
    ): StoryModel {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm.ss.SSSZ")
        val likeByStoryId = likes.associateBy { it.storyId }
        val commentByStoryId = comments.associateBy { it.storyId }
        return StoryModel(
            id = story.id,
            content = story.content,
            title = nullToEmpty(story.title),
            tagline = nullToEmpty(story.tagline),
            contentType = story.contentType,
            thumbnailUrl = story.thumbnailUrl,
            thumbnailLargeUrl = generateThubmailUrl(story.thumbnailUrl, false),
            thumbnailLargeHeight = thumbnailHeight(false),
            thumbnailLargeWidth = thumbnailWidth(false),
            thumbnailSmallUrl = generateThubmailUrl(story.thumbnailUrl, true),
            thumbnailSmallHeight = thumbnailHeight(true),
            thumbnailSmallWidth = thumbnailWidth(true),
            thumbnailImage = htmlImageMapper.toHtmlImageMapper(story.thumbnailUrl),
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            sourceSite = story.sourceSite,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = nullToEmpty(story.summary),
            user = if (user == null) UserModel(id = story.userId) else user,
            status = story.status,
            draft = story.status == StoryStatus.draft,
            published = story.status == StoryStatus.published,
            modificationDateTime = moment.format(story.modificationDateTime),
            modificationDateTimeAsDate = story.modificationDateTime,
            creationDateTime = moment.format(story.creationDateTime),
            publishedDateTimeAsDate = story.publishedDateTime,
            publishedDateTime = moment.format(story.publishedDateTime),
            creationDateTimeISO8601 = fmt.format(story.creationDateTime),
            publishedDateTimeISO8601 = if (story.publishedDateTime == null) null else fmt.format(story.publishedDateTime),
            modificationDateTimeISO8601 = fmt.format(story.modificationDateTime),
            readabilityScore = story.readabilityScore,
            slug = story.slug,
            url = "$serverUrl${story.slug}",
            tags = story.tags
                .sortedByDescending { it.totalStories }
                .take(MAX_TAGS)
                .map { tagMapper.toTagModel(it) },
            topic = if (story.topic == null) TopicModel() else topicMapper.toTopicMmodel(story.topic!!),
            liveDateTime = moment.format(story.liveDateTime),
            live = story.live,
            wppStatus = story.wppStatus,
            socialMediaMessage = story.socialMediaMessage,
            scheduledPublishDateTime = formatMediumDate(story.scheduledPublishDateTime),
            scheduledPublishDateTimeAsDate = story.scheduledPublishDateTime,
            publishToSocialMedia = story.publishToSocialMedia,
            access = story.access,
            likeCount = likeByStoryId[story.id]?.count ?: 0,
            liked = likeByStoryId[story.id]?.liked ?: false,
            pinned = pinnedStoryId == story.id,
            commentCount = commentByStoryId[story.id]?.count ?: 0,
            commented = commentByStoryId[story.id]?.commented ?: false,
        )
    }

    fun toStoryModel(
        story: StorySummaryDto,
        user: UserModel? = null,
        pinnedStoryId: Long? = null,
        likes: List<LikeCounter>,
        comments: List<CommentCounter>,
    ): StoryModel {
        val likeByStoryId = likes.associateBy { it.storyId }
        val commentByStoryId = comments.associateBy { it.storyId }
        return StoryModel(
            id = story.id,
            title = nullToEmpty(story.title),
            tagline = nullToEmpty(story.tagline),
            thumbnailUrl = story.thumbnailUrl,
            thumbnailLargeUrl = generateThubmailUrl(story.thumbnailUrl, false),
            thumbnailLargeHeight = thumbnailHeight(false),
            thumbnailLargeWidth = thumbnailWidth(false),
            thumbnailSmallUrl = generateThubmailUrl(story.thumbnailUrl, true),
            thumbnailSmallHeight = thumbnailHeight(true),
            thumbnailSmallWidth = thumbnailWidth(true),
            thumbnailImage = htmlImageMapper.toHtmlImageMapper(story.thumbnailUrl),
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = nullToEmpty(story.summary),
            user = user ?: UserModel(id = story.userId),
            status = story.status,
            draft = story.status == StoryStatus.draft,
            published = story.status == StoryStatus.published,
            modificationDateTime = moment.format(story.modificationDateTime),
            modificationDateTimeAsDate = story.modificationDateTime,
            creationDateTime = moment.format(story.creationDateTime),
            publishedDateTime = moment.format(story.publishedDateTime),
            publishedDateTimeAsDate = story.publishedDateTime,
            slug = story.slug,
            url = "$serverUrl${story.slug}",
            topic = if (story.topicId == null) TopicModel() else nullToEmpty(topicService.get(story.topicId!!)),
            liveDateTime = moment.format(story.liveDateTime),
            live = story.live,
            wppStatus = story.wppStatus,
            pinned = pinnedStoryId == story.id,
            scheduledPublishDateTime = formatMediumDate(story.scheduledPublishDateTime),
            scheduledPublishDateTimeAsDate = story.scheduledPublishDateTime,
            access = story.access,
            likeCount = likeByStoryId[story.id]?.count ?: 0,
            liked = likeByStoryId[story.id]?.liked ?: false,
            commentCount = commentByStoryId[story.id]?.count ?: 0,
            commented = commentByStoryId[story.id]?.commented ?: false,
        )
    }

    fun toReadabilityModel(obj: ReadabilityDto) = ReadabilityModel(
        score = obj.score,
        scoreThreshold = obj.scoreThreshold,
        color = readabilityColor(obj.score),
        rules = obj.rules.map {
            ReadabilityRuleModel(
                name = it.name,
                score = it.score,
                color = readabilityColor(it.score),
            )
        },
    )

    private fun nullToEmpty(value: String?): String = value ?: ""

    private fun nullToEmpty(topic: TopicModel?): TopicModel = topic ?: TopicModel()

    private fun readabilityColor(score: Int): String {
        if (score <= 50) {
            return "red"
        } else if (score <= 75) {
            return "yellow"
        } else {
            return "green"
        }
    }

    private fun formatMediumDate(date: Date?): String {
        date ?: return ""

        val fmt = DateFormat.getDateInstance(DateFormat.MEDIUM, localizationService.getLocale())
        return fmt.format(date)
    }

    private fun generateThubmailUrl(url: String?, small: Boolean): String? {
        if (url.isNullOrEmpty()) {
            return null
        }

        if (!requestContext.isMobileUserAgent()) {
            if (small) {
                return imageKit.transform(
                    url = url,
                    transformation = Transformation(
                        Dimension(width = desktopThumbnailSmallWidth, height = desktopThumbnailSmallHeight),
                        focus = Focus.AUTO,
                    ),
                )
            } else {
                return imageKit.transform(
                    url = url,
                    transformation = Transformation(
                        Dimension(width = desktopThumbnailLargeWidth),
                        focus = Focus.AUTO,
                    ),
                )
            }
        } else {
            if (small) {
                return imageKit.transform(
                    url = url,
                    transformation = Transformation(
                        Dimension(width = mobileThumbnailSmallWidth),
                        focus = Focus.AUTO,
                    ),
                )
            } else {
                return imageKit.transform(
                    url = url,
                    transformation = Transformation(
                        Dimension(width = mobileThumbnailLargeWidth),
                        focus = Focus.AUTO,
                    ),
                )
            }
        }
    }

    private fun thumbnailWidth(small: Boolean): Int? {
        if (!requestContext.isMobileUserAgent()) {
            return if (small) desktopThumbnailSmallWidth else desktopThumbnailLargeWidth
        }

        return if (small) mobileThumbnailSmallWidth else mobileThumbnailLargeWidth
    }

    private fun thumbnailHeight(small: Boolean): Int? {
        if (!requestContext.isMobileUserAgent()) {
            return if (small) desktopThumbnailSmallHeight else desktopThumbnailLargeHeight
        }

        return if (small) mobileThumbnailSmallHeight else mobileThumbnailLargeHeight
    }
}
