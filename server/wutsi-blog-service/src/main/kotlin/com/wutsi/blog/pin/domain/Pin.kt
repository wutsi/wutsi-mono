package com.wutsi.blog.pin.domain

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_PIN")
class Pin(
    @Id
    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "story_fk")
    var storyId: Long = -1,

    var creationDateTime: Date = Date(),
)
