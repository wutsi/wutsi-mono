package com.wutsi.blog.like.domain

import com.wutsi.blog.account.domain.User
import com.wutsi.blog.story.domain.Story
import java.util.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_LIKE")
class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_fk")
    val user: User? = null,

    val deviceId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_fk")
    val story: Story = Story(),

    val likeDateTime: Date = Date(),
)
