package com.wutsi.blog.story.domain

import com.wutsi.blog.client.story.StoryAccess
import com.wutsi.blog.client.story.StoryAccess.PUBLIC
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.WPPStatus
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
data class Story(
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
    var tags: List<Tag> = emptyList(),

    @Column(name = "topic_fk")
    var topicId: Long? = null,

    @Enumerated
    var status: StoryStatus = StoryStatus.draft,

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
    var live: Boolean = false,
    var liveDateTime: Date? = null,

    @Deprecated("WPP no longer supported")
    @Enumerated
    var wppStatus: WPPStatus? = null,

    @Deprecated("WPP no longer supported")
    var wppRejectionReason: String? = null,

    @Deprecated("WPP no longer supported")
    var wppModificationDateTime: Date? = null,

    var deleted: Boolean = false,
    var deletedDateTime: Date? = null,
    var publishToSocialMedia: Boolean? = null,
    var socialMediaMessage: String? = null,
    var scheduledPublishDateTime: Date? = null,

    @Enumerated
    var access: StoryAccess = PUBLIC,

    val siteId: Long = -1,
)
