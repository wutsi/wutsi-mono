package com.wutsi.blog.endorsement.dto

data class SearchEndorsementResponse(
    val endorsements: List<Endorsement> = emptyList(),
)
