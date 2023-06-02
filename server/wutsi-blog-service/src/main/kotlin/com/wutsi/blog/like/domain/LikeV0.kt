package com.wutsi.blog.like.domain

import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.user.domain.UserEntity
import java.util.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Deprecated("")
@Entity
@Table(name = "T_LIKE")
class LikeV0(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_fk")
    val user: UserEntity? = null,

    val deviceId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_fk")
    val story: Story = Story(),

    val likeDateTime: Date = Date(),
)
