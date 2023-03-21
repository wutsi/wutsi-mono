package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.GetAccountDeviceDelegate
import com.wutsi.membership.access.dto.GetAccountDeviceResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetAccountDeviceController(
    public val `delegate`: GetAccountDeviceDelegate,
) {
    @GetMapping("/v1/accounts/{id}/device")
    public fun invoke(@PathVariable(name = "id") id: Long): GetAccountDeviceResponse =
        delegate.invoke(id)
}
