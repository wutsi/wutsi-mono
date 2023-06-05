package com.wutsi.blog.app.page.reader.view

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Description
import com.rometools.rome.feed.rss.Enclosure
import com.rometools.rome.feed.rss.Item
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import java.net.URLConnection
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StoryRssView(
    private val user: UserModel? = null,
    private val startDate: Date? = null,
    private val endDate: Date? = null,
    private val storyService: StoryService,
    private val baseUrl: String,
) : AbstractRssFeedView() {
    override fun buildFeedMetadata(model: MutableMap<String, Any>, feed: Channel, request: HttpServletRequest) {
        feed?.title = getTitle()
        feed?.description = getDescription()
        feed?.link = getLink()
    }

    override fun buildFeedItems(
        model: MutableMap<String, Any>,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): MutableList<Item> {
        val items = mutableListOf<Item>()
        val stories = findStories()
        stories.forEach {
            val description = Description()
            description.value = it.summary

            val item = Item()
            item.author = it.user.fullName
            item.title = it.title
            item.link = "${baseUrl}${it.slug}"
            item.description = description
            item.pubDate = it.publishedDateTimeAsDate
            toEnclosure(it.thumbnailUrl)?.let {
                item.enclosures = listOf(it)
            }

            items.add(item)
        }
        return items
    }

    private fun toEnclosure(url: String?): Enclosure? {
        if (url.isNullOrEmpty()) {
            return null
        }

        val enclosure = Enclosure()
        enclosure.url = url
        enclosure.type = URLConnection.guessContentTypeFromName(url)
        return enclosure
    }

    private fun getTitle(): String =
        user?.let { "${it.fullName}(@${it.name}) RSS Feed" } ?: "Wutsi RSS Feed"

    private fun getDescription(): String? =
        user?.let { it.biography } ?: "Wutsi RSS Feed"

    private fun getLink(): String =
        if (user == null) baseUrl else "$baseUrl${user.slug}"

    private fun findStories(): List<StoryModel> = storyService.search(
        SearchStoryRequest(
            userIds = user?.let { listOf(it.id) } ?: emptyList(),
            publishedStartDate = startDate,
            publishedEndDate = endDate,
            status = StoryStatus.PUBLISHED,
            live = true,
            limit = 10,
            sortBy = StorySortStrategy.published,
            sortOrder = SortOrder.descending,
        ),
    )
}
