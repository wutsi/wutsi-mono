package com.wutsi.application.web.model

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("urlset")
data class SitemapModel(
    val url: List<UrlModel> = mutableListOf(),
)
