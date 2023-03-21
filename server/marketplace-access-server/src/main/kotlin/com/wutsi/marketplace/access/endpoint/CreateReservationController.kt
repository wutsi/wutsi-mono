package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CreateReservationDelegate
import com.wutsi.marketplace.access.dto.CreateReservationRequest
import com.wutsi.marketplace.access.dto.CreateReservationResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateReservationController(
    public val `delegate`: CreateReservationDelegate,
) {
    @PostMapping("/v1/reservations")
    public fun invoke(
        @Valid @RequestBody
        request: CreateReservationRequest,
    ):
        CreateReservationResponse = delegate.invoke(request)
}
