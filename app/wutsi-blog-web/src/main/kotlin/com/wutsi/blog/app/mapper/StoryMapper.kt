package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.ReadabilityModel
import com.wutsi.blog.app.model.ReadabilityRuleModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.TopicModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.LocalizationService
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TopicService
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.dto.Readability
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

    @Value("\${wutsi.image.story.desktop.medium.width}") private val desktopImageMediumWidth: Int,
    @Value("\${wutsi.image.story.desktop.medium.height}") private val desktopImageMediumHeight: Int,
    @Value("\${wutsi.image.story.desktop.small.width}") private val desktopImageSmallWidth: Int,
    @Value("\${wutsi.image.story.desktop.small.height}") private val desktopImageSmallHeight: Int,

    @Value("\${wutsi.image.story.mobile.medium.width}") private val mobileImageMediumWidth: Int,
    @Value("\${wutsi.image.story.mobile.medium.height}") private val mobileImageMediumHeight: Int,
    @Value("\${wutsi.image.story.mobile.small.width}") private val mobileImageSmallWidth: Int,
    @Value("\${wutsi.image.story.mobile.small.height}") private val mobileImageSmallHeight: Int,

    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) {
    companion object {
        const val MAX_TAGS: Int = 5
    }

    fun toStoryModel(
        story: Story,
        user: UserModel? = null,
    ): StoryModel {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm.ss.SSSZ")
        return StoryModel(
            id = story.id,
            content = story.content,
            title = nullToEmpty(story.title),
            tagline = nullToEmpty(story.tagline),
            contentType = story.contentType,
            thumbnailUrl = story.thumbnailUrl,
            thumbnailLargeUrl = generateThumbnailUrl(story.thumbnailUrl, false),
            thumbnailLargeHeight = getThumbnailHeight(false),
            thumbnailLargeWidth = getThumbnailWidth(false),
            thumbnailSmallUrl = generateThumbnailUrl(story.thumbnailUrl, true),
            thumbnailSmallHeight = getThumbnailHeight(true),
            thumbnailSmallWidth = getThumbnailWidth(true),
            thumbnailImage = htmlImageMapper.toHtmlImageMapper(story.thumbnailUrl),
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            sourceSite = story.sourceSite,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = nullToEmpty(story.summary),
            user = user ?: UserModel(id = story.userId),
            status = story.status,
            draft = story.status == StoryStatus.DRAFT,
            published = story.status == StoryStatus.PUBLISHED,
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
            scheduledPublishDateTime = formatMediumDate(story.scheduledPublishDateTime),
            scheduledPublishDateTimeAsDate = story.scheduledPublishDateTime,
            access = story.access,
            likeCount = story.likeCount,
            liked = story.liked,
            pinned = story.pinned,
            commentCount = story.commentCount,
            commented = story.commented,
            shareCount = story.shareCount,
            shared = story.shared,
            readCount = story.readCount,
            video = story.video,
        )
    }

    fun toStoryModel(
        story: StorySummary,
        user: UserModel? = null,
        pinnedStoryId: Long? = null,
    ): StoryModel {
        return StoryModel(
            id = story.id,
            title = nullToEmpty(story.title),
            tagline = nullToEmpty(story.tagline),
            thumbnailUrl = story.thumbnailUrl,
            thumbnailLargeUrl = generateThumbnailUrl(story.thumbnailUrl, false),
            thumbnailLargeHeight = getThumbnailHeight(false),
            thumbnailLargeWidth = getThumbnailWidth(false),
            thumbnailSmallUrl = generateThumbnailUrl(story.thumbnailUrl, true),
            thumbnailSmallHeight = getThumbnailHeight(true),
            thumbnailSmallWidth = getThumbnailWidth(true),
            thumbnailImage = htmlImageMapper.toHtmlImageMapper(story.thumbnailUrl),
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = nullToEmpty(story.summary),
            user = user ?: UserModel(id = story.userId),
            status = story.status,
            draft = story.status == StoryStatus.DRAFT,
            published = story.status == StoryStatus.PUBLISHED,
            modificationDateTime = moment.format(story.modificationDateTime),
            modificationDateTimeAsDate = story.modificationDateTime,
            creationDateTime = moment.format(story.creationDateTime),
            publishedDateTime = moment.format(story.publishedDateTime),
            publishedDateTimeAsDate = story.publishedDateTime,
            slug = story.slug,
            url = "$serverUrl${story.slug}",
            topic = if (story.topicId == null) TopicModel() else nullToEmpty(topicService.get(story.topicId!!)),
            scheduledPublishDateTime = formatMediumDate(story.scheduledPublishDateTime),
            scheduledPublishDateTimeAsDate = story.scheduledPublishDateTime,
            access = story.access,
            pinned = pinnedStoryId == story.id,
            likeCount = story.likeCount,
            liked = story.liked,
            commentCount = story.commentCount,
            commented = story.commented,
            shareCount = story.shareCount,
            shared = story.shared,
            readCount = story.readCount,
            video = story.video,
        )
    }

    fun toReadabilityModel(obj: Readability) = ReadabilityModel(
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

    private fun generateThumbnailUrl(url: String?, small: Boolean): String? {
        if (url.isNullOrEmpty()) {
            return null
        }

        return imageKit.transform(
            url = url,
            transformation = Transformation(
                Dimension(width = getThumbnailWidth(small), height = getThumbnailHeight(small)),
                focus = Focus.TOP,
            ),
        )
    }

    private fun getThumbnailWidth(small: Boolean): Int {
        if (!requestContext.isMobileUserAgent()) {
            return if (small) desktopImageSmallWidth else desktopImageMediumWidth
        }

        return if (small) mobileImageSmallWidth else mobileImageMediumWidth
    }

    private fun getThumbnailHeight(small: Boolean): Int {
        if (!requestContext.isMobileUserAgent()) {
            return if (small) desktopImageSmallHeight else desktopImageMediumHeight
        }

        return if (small) mobileImageSmallHeight else mobileImageMediumHeight
    }
}
