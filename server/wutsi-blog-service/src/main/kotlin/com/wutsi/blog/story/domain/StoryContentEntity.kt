package com.wutsi.blog.story.domain

import java.util.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "T_STORY_CONTENT")
data class StoryContentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_fk")
    val story: StoryEntity = StoryEntity(),

    var title: String? = null,
    var tagline: String? = null,
    var summary: String? = null,
    var language: String? = null,
    var content: String? = null,
    var contentType: String? = null,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
