package com.wutsi.blog.story.domain

import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.WPPStatus
import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

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
)
