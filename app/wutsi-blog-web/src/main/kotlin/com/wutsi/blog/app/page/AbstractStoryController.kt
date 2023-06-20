package com.wutsi.blog.app.page

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
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
