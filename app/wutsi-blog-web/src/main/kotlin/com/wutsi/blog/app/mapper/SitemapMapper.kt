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
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    fun toUrlModel(path: String) = UrlModel(
        loc = "${baseUrl}$path",
        lastmod = SimpleDateFormat(DATE_FORMAT).format(Date()),
    )

    fun toUrlModel(story: StorySummary) = UrlModel(
        loc = "${baseUrl}${story.slug}",
        lastmod = SimpleDateFormat(DATE_FORMAT).format(story.contentModificationDateTime),
    )

    fun toUrlModel(user: UserSummary) = UrlModel(
        loc = baseUrl + userMapper.slug(user),
    )
}
