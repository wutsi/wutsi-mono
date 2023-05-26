package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RecommendationController(
    private val storyService: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecommendationController::class.java)
    }

    override fun pageName() = PageName.RECOMMEND

    @GetMapping("/recommend")
    fun recommend(
        @RequestParam storyId: Long,
        @RequestParam(required = false, defaultValue = "summary") layout: String = "summary",
        model: Model,
    ): String {
        try {
            val stories = storyService.recommend(storyId, 20)
            model.addAttribute("stories", stories.take(5))
            if (stories.isNotEmpty()) {
                val story = storyService.get(storyId)
                model.addAttribute("blog", story.user)
                model.addAttribute("layout", layout)
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find Story recommendations", ex)
        }
        return "page/story/recommend"
    }
}
