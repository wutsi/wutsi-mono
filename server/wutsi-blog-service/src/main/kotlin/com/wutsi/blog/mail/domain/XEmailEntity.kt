package com.wutsi.blog.mail.domain

import com.wutsi.blog.mail.dto.NotificationType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_XEMAIL")
data class XEmailEntity(
    @Id
    val id: String? = null,

    val email: String = "",
    val type: NotificationType = NotificationType.UNKNOWN,
    val creationDateTime: Date = Date(),
)
