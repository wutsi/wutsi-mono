package com.wutsi.security.manager.entity

import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_KEY")
data class KeyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val algorithm: String = "",
    val publicKey: String = "",
    val privateKey: String = "",
    val created: Date = Date(),
    val expires: Date = Date(),
)
