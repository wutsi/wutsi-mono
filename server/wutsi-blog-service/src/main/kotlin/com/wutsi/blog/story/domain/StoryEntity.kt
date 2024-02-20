package com.wutsi.blog.story.domain

import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.WPPStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_STORY")
data class StoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "T_STORY_TAG",
        joinColumns = arrayOf(JoinColumn(name = "story_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "tag_fk")),
    )
    var tags: List<TagEntity> = emptyList(),

    @Column(name = "topic_fk")
    var topicId: Long? = null,

    @Enumerated
    var status: StoryStatus = StoryStatus.DRAFT,

    var title: String? = null,
    var tagline: String? = null,
    var summary: String? = null,
    var thumbnailUrl: String? = null,
    var sourceUrl: String? = null,
    var sourceSite: String? = null,
    var sourceUrlHash: String? = null,
    var wordCount: Int = 0,
    var readingMinutes: Int = 0,
    var language: String? = null,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
    var publishedDateTime: Date? = null,
    var readabilityScore: Int = -1,
    var likeCount: Long = 0,
    var commentCount: Long = 0,
    var shareCount: Long = 0,
    var readCount: Long = 0,
    var video: Boolean? = null,
    var subscriberReaderCount: Long = 0,
    var attachmentDownloadCount: Long = 0,

    var deleted: Boolean = false,
    var deletedDateTime: Date? = null,

    @Deprecated("")
    var publishToSocialMedia: Boolean? = null,

    @Deprecated("")
    var socialMediaMessage: String? = null,

    var scheduledPublishDateTime: Date? = null,

    @Enumerated
    var access: StoryAccess = PUBLIC,

    @Deprecated("No tenant supported")
    val siteId: Long = -1,

    var recipientCount: Long = 0,
    var totalDurationSeconds: Long = 0,
    var subscriberCount: Long = 0, // Number of subscription created by this story

    var clickCount: Long = 0,
    var readerCount: Long = 0,
    var emailReaderCount: Long = 0,

    var wppScore: Int = 0,

    @Deprecated("")
    var liveDateTime: Date? = null,

    @Deprecated("Status.published is enough")
    var live: Boolean = false,

    @Deprecated("WPP no longer supported")
    @Enumerated
    var wppStatus: WPPStatus? = null,

    @Deprecated("WPP no longer supported")
    var wppRejectionReason: String? = null,

    @Deprecated("WPP no longer supported")
    var wppModificationDateTime: Date? = null,

    @Deprecated("Replace by WPP score")
    var wpp: Boolean = false,

    var contentModificationDateTime: Date = Date(),
)
