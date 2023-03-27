package com.wutsi.application.membership.onboard.page

import com.wutsi.application.Theme
import com.wutsi.error.ErrorURN
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.CreateMemberRequest
import com.wutsi.platform.core.error.ErrorResponse
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/onboard/pages/review")
class Onboard05ReviewPage(
    private val membershipManagerApi: MembershipManagerApi,
    @Value("\${wutsi.application.website-url}") private val websiteUrl: String,
) : AbstractOnboardPage() {
    companion object {
        const val PAGE_INDEX = 5
    }

    @PostMapping
    fun index(): Widget {
        val data = onboardDao.get()
        return Column(
            children = listOf(
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        IconButton(
                            icon = Theme.ICON_ARROW_BACK,
                            color = Theme.COLOR_BLACK,
                            action = gotoPage(PAGE_INDEX - 1),
                        ),
                    ),
                ),
                Container(
                    alignment = Center,
                    padding = 20.0,
                    child = Column(
                        children = listOfNotNull(
                            Container(
                                alignment = Center,
                                padding = 10.0,
                                child = Image(
                                    url = getLogoUrl(),
                                    width = 128.0,
                                    height = 128.0,
                                ),
                            ),
                            Container(
                                alignment = TopCenter,
                                child = Text(
                                    caption = data.displayName,
                                    alignment = TextAlignment.Center,
                                    size = Theme.TEXT_SIZE_LARGE,
                                    color = Theme.COLOR_PRIMARY,
                                    bold = true,
                                ),
                            ),
                            Container(
                                alignment = TopCenter,
                                child = Text(
                                    caption = formattedPhoneNumber(data.phoneNumber, data.country) ?: "",
                                    alignment = TextAlignment.Center,
                                    size = Theme.TEXT_SIZE_LARGE,
                                ),
                            ),
                            Container(
                                padding = 20.0,
                            ),
                            Container(
                                padding = 10.0,
                                child = Text(
                                    caption = getText("page.final.terms-condition-message"),
                                    alignment = TextAlignment.Center,
                                ),
                            ),
                            Container(
                                padding = 10.0,
                            ),
                            Button(
                                id = "create-wallet",
                                caption = getText("page.final.button.submit"),
                                action = Action(
                                    type = ActionType.Command,
                                    url = urlBuilder.build("/onboard/pages/review/submit"),
                                ),
                            ),
                            Button(
                                caption = getText("page.final.button.view-terms"),
                                action = Action(
                                    type = ActionType.Navigate,
                                    url = getTermOfServiceUrl(data.language),
                                ),
                                type = ButtonType.Text,
                            ),
                            Button(
                                caption = getText("page.final.button.cancel"),
                                action = gotoPreviousScreen(),
                                type = ButtonType.Outlined,
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(): Action {
        try {
            val data = onboardDao.get()
            membershipManagerApi.createMember(
                request = CreateMemberRequest(
                    phoneNumber = data.phoneNumber,
                    displayName = data.displayName,
                    country = data.country,
                    pin = data.pin,
                ),
            )
        } catch (ex: FeignException) {
            val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            if (ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn != response.error.code) {
                throw ex
            }
        }
        return gotoPage(PAGE_INDEX + 1)
    }

    private fun getTermOfServiceUrl(language: String): String =
        if (language == "fr") {
            "$websiteUrl/confidentialite/"
        } else {
            "$websiteUrl/terms-and-conditions/"
        }
}
