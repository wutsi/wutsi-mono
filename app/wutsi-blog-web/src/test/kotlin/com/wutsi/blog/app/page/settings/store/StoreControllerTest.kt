package com.wutsi.blog.app.page.settings.store

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.CreateStoreCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StoreControllerTest : SeleniumTestSupport() {
    @Test
    fun create() {
        // GIVEN
        val user = setupLoggedInUser(100, blog = true, walletId = "wallet-id")

        // WHEN
        navigate(url("/store"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_STORE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.SETTINGS_STORE_CREATE)
        assertElementNotPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
        click("#btn-create")

        assertCurrentPageIs(PageName.SETTINGS_STORE_SUCCESS)
        val command = argumentCaptor<CreateStoreCommand>()
        verify(storeBackend).create(command.capture())
        assertEquals(user.id, command.firstValue.userId)
    }

    @Test
    fun `non blog cannot create store`() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = "wallet-id")

        // WHEN
        navigate(url("/store"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_STORE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.SETTINGS_STORE_CREATE)
        assertElementNotPresent("#btn-continue")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `blog without monetization cannot creaet store`() {
        // GIVEN
        setupLoggedInUser(100, blog = true, walletId = null)

        // WHEN
        navigate(url("/store"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_STORE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.SETTINGS_STORE_CREATE)
        assertElementNotPresent("#btn-continue")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `already member`() {
        // GIVEN
        setupLoggedInUser(100, blog = true, walletId = "wallet-id", storeId = "store-id")

        // WHEN
        navigate(url("/store"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_STORE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.SETTINGS_STORE_CREATE)
        assertElementNotPresent("#btn-continue")
        assertElementNotPresent(".alert-danger")
        assertElementPresent(".alert-success")
    }
}
