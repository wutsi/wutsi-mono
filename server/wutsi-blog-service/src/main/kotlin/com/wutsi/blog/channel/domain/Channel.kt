package com.wutsi.blog.channel.domain

import com.wutsi.blog.client.channel.ChannelType
import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_CHANNEL")
data class Channel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Enumerated
    val type: ChannelType = ChannelType.unknown,

    val providerUserId: String = "",
    val name: String = "",
    val pictureUrl: String? = null,
    val accessToken: String = "",
    val accessTokenSecret: String = "",
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
