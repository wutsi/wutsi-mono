package com.wutsi.blog.app.page.create

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import org.junit.jupiter.api.Test

class CreateControllerTest : SeleniumTestSupport() {
    @Test
    fun create() {
        // GIVEN
        val userId = 1L
        setupLoggedInUser(userId)

        // Blog name
        driver.get("$url/create")
        assertCurrentPageIs(PageName.CREATE)
        input("input[name=value]", "new-blog")
        click("#btn-next")
        verify(userBackend).updateAttribute(UpdateUserAttributeCommand(userId, "name", "new-blog"))

        // Blog email
        assertCurrentPageIs(PageName.CREATE_EMAIL)
        input("input[name=value]", "new-blog@gmail.com")
        click("#btn-next")
        verify(userBackend).updateAttribute(UpdateUserAttributeCommand(userId, "email", "new-blog@gmail.com"))

        // Country
        assertCurrentPageIs(PageName.CREATE_COUNTRY)
        click("#btn-next")

        // Review
        assertCurrentPageIs(PageName.CREATE_REVIEW)
        click("#btn-create")
        verify(userBackend).createBlog(CreateBlogCommand(userId))

        // Success
        assertCurrentPageIs(PageName.CREATE_SUCCESS)
    }
}
