package com.wutsi.membership.manager.dto

import kotlin.collections.List

public data class SearchMemberResponse(
    public val members: List<MemberSummary> = emptyList(),
)
