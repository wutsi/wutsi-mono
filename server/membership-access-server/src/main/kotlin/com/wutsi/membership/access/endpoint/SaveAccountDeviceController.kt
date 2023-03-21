package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.SaveAccountDeviceDelegate
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class SaveAccountDeviceController(
    public val `delegate`: SaveAccountDeviceDelegate,
) {
    @PostMapping("/v1/accounts/{id}/device")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: SaveAccountDeviceRequest,
    ) {
        delegate.invoke(id, request)
    }
}
