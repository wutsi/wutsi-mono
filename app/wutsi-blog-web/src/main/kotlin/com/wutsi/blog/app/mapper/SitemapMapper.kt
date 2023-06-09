package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.UrlModel
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.dto.UserSummary
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class SitemapMapper(
    private val userMapper: UserMapper,
    @Value("\${wutsi.application.server-url}") private val baseUrl: String,
) {
    fun toUrlModel(path: String) = UrlModel(
        loc = "${baseUrl}$path",
        lastmod = SimpleDateFormat("yyyy-MM-dd").format(Date()),
    )

    fun toUrlModel(story: StorySummary) = UrlModel(
        loc = "${baseUrl}${story.slug}",
        lastmod = SimpleDateFormat("yyyy-MM-dd").format(story.modificationDateTime),
    )

    fun toUrlModel(user: UserSummary) = UrlModel(
        loc = baseUrl + userMapper.slug(user),
        lastmod = SimpleDateFormat("yyyy-MM-dd").format(Date()),
    )
}
