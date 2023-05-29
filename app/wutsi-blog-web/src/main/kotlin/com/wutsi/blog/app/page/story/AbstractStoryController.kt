package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService

abstract class AbstractStoryController(
    protected val service: StoryService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {

    protected abstract fun requiredPermissions(): List<Permission>

    private fun checkAccess(story: StoryModel) {
        requestContext.checkAccess(story, requiredPermissions())
    }

    protected fun getStory(id: Long): StoryModel {
        val story = service.get(id)
        checkAccess(story)

        return story
    }
}
