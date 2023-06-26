package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/inbox")
class InboxController(
    private val storyService: StoryService,
    private val subscriptionService: SubscriptionService,
    private val userService: UserService,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT: Int = 20
    }

    override fun pageName() = PageName.INBOX

    override fun shouldBeIndexedByBots() = false

    override fun shouldShowGoogleOneTap() = false

    @GetMapping
    fun index(model: Model): String {
        val me = requestContext.currentUser()
        if (me != null) {
            val subscriptions = subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = me.id,
                    limit = 100,
                ),
            )

            val excludeUserIds = subscriptions.map { it.userId }.toMutableList()
            excludeUserIds.add(me.id)
            val blogs = userService.search(
                SearchUserRequest(
                    excludeUserIds = excludeUserIds,
                    sortBy = UserSortStrategy.POPULARITY,
                    sortOrder = SortOrder.DESCENDING,
                    active = true,
                    blog = true,
                    withPublishedStories = true,
                    limit = 5,
                ),
            ).map { it.copy(slug = "${it.slug}?utm_from=inbox") }
            model.addAttribute("blogs", blogs)

            stories(0, model)
            model.addAttribute("page", createPage())
        }

        return "reader/inbox"
    }

    @GetMapping("/stories")
    fun stories(@RequestParam offset: Int, model: Model): String {
        val me = requestContext.currentUser()
        if (me != null) {
            val subscriptionIds = subscriptionService.search(
                SearchSubscriptionRequest(
                    subscriberId = me.id,
                    limit = 50,
                ),
            ).map { it.userId }

            if (subscriptionIds.isNotEmpty()) {
                // Stories
                val stories = storyService.search(
                    SearchStoryRequest(
                        userIds = subscriptionIds,
                        sortBy = StorySortStrategy.PUBLISHED,
                        sortOrder = SortOrder.DESCENDING,
                        limit = LIMIT,
                        offset = offset,
                    ),
                ).map { it.copy(slug = "${it.slug}?utm_from=inbox") }
                if (stories.isNotEmpty()) {
                    model.addAttribute("stories", stories)
                    model.addAttribute("cardType", "summary")
                    if (stories.size >= LIMIT) {
                        model.addAttribute("moreUrl", "/inbox/stories?offset=" + (LIMIT + offset))
                    }
                }
            }
        }
        return "reader/fragment/stories"
    }

    private fun createPage() = createPage(
        name = pageName(),
        title = requestContext.getMessage("page.inbox.title"),
        description = "",
    )
}
