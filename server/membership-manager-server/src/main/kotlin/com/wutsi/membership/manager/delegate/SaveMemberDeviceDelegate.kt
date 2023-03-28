package com.wutsi.membership.manager.delegate

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import com.wutsi.membership.manager.util.SecurityUtil
import org.springframework.stereotype.Service

@Service
public class SaveMemberDeviceDelegate(private val membershipAccessApi: MembershipAccessApi) {
    public fun invoke(request: SaveDeviceRequest) {
        membershipAccessApi.saveAccountDevice(
            id = SecurityUtil.getAccountId(),
            request = SaveAccountDeviceRequest(
                token = request.token,
                type = request.type,
                osVersion = request.osVersion,
                osName = request.osName,
                model = request.model,
            ),
        )
    }
}
