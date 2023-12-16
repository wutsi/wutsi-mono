package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.Subscription
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.CreateWalletResponse
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.UpdateWalletAccountCommand
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.UUID
import kotlin.io.path.deleteIfExists
import kotlin.io.path.name
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
        assertElementPresent("#subscription-container")

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
        assertElementPresent("#subscription-container")

        click("#menu-item-general", 2000)
        testUpdate(user.id, "name", user.name, "roger milla", expectedValue = "rogermilla")
        testUpdate(user.id, "biography", user.biography, "New biography...")
        testUpdate(user.id, "website_url", user.websiteUrl, "https://www.roger-milla.com")

        click("#menu-item-social-media", 2000)
        testUpdate(user.id, "facebook_id", user.facebookId, "@roger", expectedValue = "roger")
        testUpdate(user.id, "youtube_id", user.youtubeId, "https://y.be/roger", expectedValue = "roger")
        testUpdate(user.id, "linkedin_id", user.linkedinId, "roger_milla111")
        testUpdate(user.id, "twitter_id", user.twitterId, "roger_milla_officiel")
        testUpdate(user.id, "github_id", user.githubId, "https://y.be/foo", expectedValue = "foo")

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
    fun setAccountNumber() {
        // GIVEN
        val user = setupLoggedInUser(100, blog = true, walletId = "1", accountNumber = "23799505677")

        // WHEN
        navigate(url("/me/settings"))

        // THEN
        click("#menu-item-wallet-account", 2000)
        testUpdate(
            user.id,
            "wallet_account_number",
            "99505677",
            "23799505688",
            walletId = "1",
        )
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
        assertEquals("settings", cmd.firstValue.referer)
        assertNull(cmd.firstValue.storyId)

        assertEquals(100L, cmd.secondValue.userId)
        assertEquals("bar@gmail.com", cmd.secondValue.email)
        assertEquals("settings", cmd.secondValue.referer)
        assertNull(cmd.secondValue.storyId)
    }

    @Test
    @Ignore
    fun updatePicture() {
        // GIVEN
        val user = setupLoggedInUser(100)
        val file = Files.createFile(File(System.getProperty("user.home"), "update-picture.png").toPath())
        try {
            val url = "https://www.img.com/1.png"
            doReturn(URL(url)).whenever(storage).store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

            // WHEN
            navigate(url("/me/settings"))
            click(".btn-upload")
            driver.switchTo().activeElement().sendKeys(file.name)

            // THEN
            verify(userBackend).updateAttribute(
                UpdateUserAttributeCommand(
                    name = "picture_url",
                    value = url,
                    userId = user.id,
                ),
            )
        } finally {
            file.deleteIfExists()
        }
    }

    @Test
    fun subscriptions() {
        // GIVEN
        setupLoggedInUser(100)

        doReturn(
            SearchSubscriptionResponse(
                listOf(
                    Subscription(userId = 111, subscriberId = 100),
                    Subscription(userId = 222, subscriberId = 100),
                )
            )
        ).whenever(subscriptionBackend).search(any())

        doReturn(
            SearchUserResponse(
                listOf(
                    UserSummary(id = 111, name = "ray.sponsible", fullName = "Ray Sponsible"),
                    UserSummary(id = 222, name = "john.smith", fullName = "John Smith"),
                )
            )
        ).whenever(userBackend).search(any())

        // WHEN
        navigate(url("/me/settings"))

        click("#menu-item-subscription", 2000)

        click("#subscription-111 .btn-unsubscribe", 500)
        val cmd = argumentCaptor<UnsubscribeCommand>()
        verify(subscriptionBackend).unsubscribe(cmd.capture())
        assertEquals(111L, cmd.firstValue.userId)
        assertEquals(100L, cmd.firstValue.subscriberId)
        assertNull(cmd.firstValue.email)
    }

    private fun testUpdate(
        userId: Long,
        name: String,
        originalValue: String?,
        newValue: String,
        error: String? = null,
        walletId: String = "",
        expectedValue: String? = null
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
        if (name == "wallet_account_number") {
            val req = argumentCaptor<UpdateWalletAccountCommand>()
            verify(walletBackend).updateAccount(req.capture())
            assertEquals(walletId, req.firstValue.walletId)
            assertEquals(PaymentMethodType.MOBILE_MONEY, req.firstValue.type)
            assertEquals("+$newValue", req.firstValue.number)
        } else {
            verify(userBackend).updateAttribute(
                UpdateUserAttributeCommand(
                    name = name,
                    value = expectedValue ?: newValue,
                    userId = userId,
                ),
            )
        }
    }
}
