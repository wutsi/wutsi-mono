package com.wutsi.blog.endorsement.dto

import java.util.Date

data class Endorsement(
    val userId: Long = -1,
    val endorserId: Long = -1,
    val blurb: String? = null,
    val creationDateTime: Date = Date(),
)
