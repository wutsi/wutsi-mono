package com.wutsi.application.membership.settings.security.screen

import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.Page
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/passcode")
class PasscodeScreen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "security/passcode/pages/pin",
            "security/passcode/pages/confirm",
            "security/passcode/pages/success",
        )
    }

    @PostMapping
    fun index() = Screen(
        id = com.wutsi.application.Page.SECURITY_PASSCODE,
        appBar = null,
        safe = true,
        child = PageView(
            children = PAGE_URLS.map {
                Page(url = urlBuilder.build(it))
            },
        ),
    ).toWidget()
}
