package com.wutsi.application.firebase

import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.firebase.dto.SubmitTokenRequest
import com.wutsi.enums.DeviceType
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/firebase")
class FirebaseController(
    private val membershipManagerApi: MembershipManagerApi,
) : AbstractEndpoint() {
    @PostMapping("/token")
    fun index(@RequestBody fcm: SubmitTokenRequest, request: HttpServletRequest) {
        membershipManagerApi.saveMemberDevice(
            request = SaveDeviceRequest(
                token = fcm.token,
                type = DeviceType.MOBILE.name,
                osVersion = request.getHeader("X-OS-Version"),
                osName = request.getHeader("X-OS"),
            ),
        )
    }
}
