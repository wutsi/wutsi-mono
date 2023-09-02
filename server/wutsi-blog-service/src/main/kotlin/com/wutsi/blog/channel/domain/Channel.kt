package com.wutsi.blog.channel.domain

import com.wutsi.blog.client.channel.ChannelType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

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
