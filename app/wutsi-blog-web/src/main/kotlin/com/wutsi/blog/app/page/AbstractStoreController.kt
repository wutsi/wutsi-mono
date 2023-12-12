package com.wutsi.blog.app.page

import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException

abstract class AbstractStoreController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    protected fun checkStoreAccess(): StoreModel {
        val blog = requestContext.currentUser()
        return checkStoreAccess(blog)
    }

    protected fun checkStoreAccess(blog: UserModel?): StoreModel {
        if (blog?.blog != true) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.USER_NOT_BLOG,
                ),
            )
        }

        return getStore(blog)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.USER_HAS_NO_STORE,
                ),
            )
    }
}
