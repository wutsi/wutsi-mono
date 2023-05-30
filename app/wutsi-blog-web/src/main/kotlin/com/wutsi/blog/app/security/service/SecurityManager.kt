package com.wutsi.blog.app.security.service

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import org.springframework.stereotype.Component

@Component
class SecurityManager {
    fun permissions(story: StoryModel, user: UserModel?): List<Permission> {
        val permissions = mutableListOf<Permission>()

        if (story.published && story.live) {
            permissions.add(Permission.reader)
        }
        if (story.user.id == user?.id) {
            permissions.add(Permission.editor)
            permissions.add(Permission.previewer)
            permissions.add(Permission.owner)
        }
        return permissions
    }
}
