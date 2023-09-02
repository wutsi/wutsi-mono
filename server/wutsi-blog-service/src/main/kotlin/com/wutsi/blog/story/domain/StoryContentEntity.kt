package com.wutsi.blog.story.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.Date

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
