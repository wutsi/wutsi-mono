package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateReservationStatusDelegate
import com.wutsi.marketplace.access.dto.UpdateReservationStatusRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateReservationStatusController(
    public val `delegate`: UpdateReservationStatusDelegate,
) {
    @PostMapping("/v1/reservations/{id}/status")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateReservationStatusRequest,
    ) {
        delegate.invoke(id, request)
    }
}
