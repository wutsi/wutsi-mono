package com.wutsi.blog.comment.domain

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Deprecated("")
@Entity
@Table(name = "T_COMMENT")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "story_fk")
    val storyId: Long = -1,

    var text: String = "",
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
