package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.SaveMemberDeviceDelegate
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SaveMemberDeviceController(
    public val `delegate`: SaveMemberDeviceDelegate,
) {
    @PostMapping("/v1/members/device")
    public fun invoke(
        @Valid @RequestBody
        request: SaveDeviceRequest,
    ) {
        delegate.invoke(request)
    }
}
