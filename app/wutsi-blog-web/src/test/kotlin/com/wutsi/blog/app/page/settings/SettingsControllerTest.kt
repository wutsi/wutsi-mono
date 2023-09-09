package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.CreateWalletResponse
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class SettingsControllerTest : SeleniumTestSupport() {
    @Test
    fun user() {
        // GIVEN
        val user = setupLoggedInUser(100)

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        assertElementPresent("#general-container")
        assertElementNotPresent("#social_media-container")
        assertElementNotPresent("#instant_messaging-container")
        assertElementNotPresent("#monetization-container")
        assertElementNotPresent("#import-container")

        testUpdate(user.id, "name", user.name, "roger-milla")
        testUpdate(user.id, "email", user.email, "roger.milla2@gmail.com")
    }

    @Test
    fun blog() {
        // GIVEN
        val user = setupLoggedInUser(100, blog = true)

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        assertElementPresent("#general-container")
        assertElementPresent("#social_media-container")
        assertElementPresent("#instant_messaging-container")
        assertElementPresent("#monetization-container")
        assertElementPresent("#import-container")

        click("#menu-item-general", 2000)
        testUpdate(user.id, "biography", user.biography, "New biography...")
        testUpdate(user.id, "website_url", user.websiteUrl, "https://www.roger-milla.com")

        click("#menu-item-social-media", 2000)
        testUpdate(user.id, "facebook_id", user.facebookId, "roger-milla")
        testUpdate(user.id, "youtube_id", user.youtubeId, "roger_milla")
        testUpdate(user.id, "linkedin_id", user.linkedinId, "roger_milla111")
        testUpdate(user.id, "twitter_id", user.twitterId, "roger_milla_officiel")
        testUpdate(user.id, "github_id", user.githubId, "roger_milla")

        click("#menu-item-instant-messaging", 2000)
        testUpdate(user.id, "whatsapp_id", user.whatsappId, "237999999999")
        testUpdate(user.id, "telegram_id", user.telegramId, "roger_the_great")

        assertElementPresent("#btn-enable-monetization")
        assertElementNotPresent("#wallet-container")
    }

    @Test
    fun enableMonetization() {
        // GIVEN
        setupLoggedInUser(100, blog = true)

        val walletId = UUID.randomUUID().toString()
        doReturn(CreateWalletResponse(walletId)).whenever(walletBackend).create(any())

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        // Monetize
        click("#btn-enable-monetization")
        assertCurrentPageIs(PageName.SETTINGS_MONETIZATION)
        click("#btn-next")

        // Country
        assertCurrentPageIs(PageName.SETTINGS_MONETIZATION_COUNTRY)
        select("select[name=code]", 1)
        click("#btn-next")

        // Review
        assertCurrentPageIs(PageName.SETTINGS_MONETIZATION_REVIEW)
        click("#btn-next")
        val cmd = argumentCaptor<CreateWalletCommand>()
        verify(walletBackend).create(cmd.capture())
        assertEquals(100, cmd.firstValue.userId)
        assertEquals(Country.all[0].code, cmd.firstValue.country)
    }

    @Test
    fun importEmails() {
        // GIVEN
        setupLoggedInUser(100, blog = true)

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS)

        click("#menu-item-import", 2000)
        input("#txt-import-email", "foo@gmail.com, bar@gmail.com")
        click("#btn-import-email-submit", 1000)

        val cmd = argumentCaptor<SubscribeCommand>()
        verify(subscriptionBackend, times(2)).subscribe(cmd.capture())

        assertEquals(100L, cmd.firstValue.userId)
        assertEquals("foo@gmail.com", cmd.firstValue.email)

        assertEquals(100L, cmd.secondValue.userId)
        assertEquals("bar@gmail.com", cmd.secondValue.email)
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
        click("$selector .btn-save", 1000)

        // Verify changes
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
