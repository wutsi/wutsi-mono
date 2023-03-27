package com.wutsi.application.membership.onboard.page

import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.membership.onboard.dao.OnboardRepository
import com.wutsi.application.membership.onboard.exception.OnboardEntityNotFoundException
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler

abstract class AbstractOnboardPage : AbstractEndpoint() {
    @Autowired
    protected lateinit var onboardDao: OnboardRepository

    @ExceptionHandler(OnboardEntityNotFoundException::class)
    fun onOnboardEntityNotFoundException(ex: OnboardEntityNotFoundException) =
        gotoUrl("/onboard/2", ActionType.Route)
}
