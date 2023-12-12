package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException

abstract class AbstractStoreController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    protected fun checkAccess() {
        val blog = requestContext.currentUser()
        if (blog?.blog != true) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.USER_NOT_BLOG,
                ),
            )
        }

        getStore(blog)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.USER_HAS_NO_STORE,
                ),
            )
    }
}
