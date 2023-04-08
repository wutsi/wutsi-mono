package com.wutsi.application.web.model

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("url")
class UrlModel(
    val loc: String = "",
    val lastmod: String? = null,
)
