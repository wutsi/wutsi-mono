package com.wutsi.blog.app.model

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "url")
@XmlAccessorType(XmlAccessType.FIELD)
class UrlModel(
    @XmlElement val loc: String = "",
    @XmlElement val lastmod: String? = null,
    @XmlElement val changefreq: String? = "daily",
)
