package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import org.junit.jupiter.api.Test
import java.util.UUID

internal class SettingsControllerTest : SeleniumTestSupport() {
    @Test
    fun user() {
        // GIVEN
        val user = setupLoggedInUser(100, false)

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        assertElementPresent("#general-container")
        assertElementNotPresent("#social_media-container")
        assertElementNotPresent("#instant_messaging-container")
        assertElementNotPresent("#monetization-container")

        testUpdate(user.id, "name", user.name, "roger-milla")
        testUpdate(user.id, "email", user.email, "roger.milla2@gmail.com")
    }

    @Test
    fun blog() {
        // GIVEN
        val user = setupLoggedInUser(100, true)

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        assertElementPresent("#general-container")
        assertElementPresent("#social_media-container")
        assertElementPresent("#instant_messaging-container")
        assertElementPresent("#monetization-container")

        testUpdate(user.id, "biography", user.biography, "roger.milla2@gmail.com")
        testUpdate(user.id, "website_url", user.websiteUrl, "https://www.roger-milla.com")
        testUpdate(user.id, "facebook_id", user.facebookId, "roger-milla")
        testUpdate(user.id, "youtube_id", user.youtubeId, "roger_milla")
        testUpdate(user.id, "linkedin_id", user.linkedinId, "roger_milla111")
        testUpdate(user.id, "twitter_id", user.twitterId, "roger_milla_officiel")
        testUpdate(user.id, "whatsapp_id", user.whatsappId, "237999999999")
        testUpdate(user.id, "telegram_id", user.telegramId, "roger_the_great")

        assertElementPresent("#btn-enable-monetization")
        assertElementNotPresent("#wallet-container")
    }

    @Test
    fun monetization() {
        // GIVEN
        val walletId = UUID.randomUUID().toString()
        val user = setupLoggedInUser(100, true, walletId)

        val wallet = Wallet(
            id = walletId,
            userId = user.id,
            balance = 30000,
            currency = "XAF",
            country = "CM",
        )
        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(walletId)

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        assertElementPresent("#general-container")
        assertElementPresent("#social_media-container")
        assertElementPresent("#instant_messaging-container")
        assertElementPresent("#monetization-container")

        assertElementNotPresent("#btn-enable-monetization")
        assertElementPresent("#wallet-container")
    }

    private fun testUpdate(
        userId: Long,
        name: String,
        originalValue: String?,
        newValue: String,
        error: String? = null,
    ) {
        val selector = "#$name-form"

        // Test current value
        assertElementAttribute("$selector .form-control", "value", originalValue ?: "")

        // Change
        click("$selector .btn-edit")
        input("$selector .form-control", newValue)
        click("$selector .btn-save")

        // Verify changes
        Thread.sleep(1000)
        assertElementAttribute("$selector .form-control", "value", newValue)
        if (error == null) {
            assertElementHasClass("$selector .alert-danger", "hidden")
        } else {
            assertElementHasNotClass("$selector .alert-danger", "hidden")
            assertElementPresent("$selector .alert-danger")
        }

        // Verify backend call
        verify(userBackend).updateAttribute(
            UpdateUserAttributeCommand(
                name = name,
                value = newValue,
                userId = userId,
            ),
        )
    }
}
