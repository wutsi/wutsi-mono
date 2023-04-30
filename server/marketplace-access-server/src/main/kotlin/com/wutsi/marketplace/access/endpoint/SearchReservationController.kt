package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.SearchReservationDelegate
import com.wutsi.marketplace.access.dto.SearchReservationRequest
import com.wutsi.marketplace.access.dto.SearchReservationResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchReservationController(
    public val `delegate`: SearchReservationDelegate,
) {
    @PostMapping("/v1/reservations/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchReservationRequest,
    ): SearchReservationResponse = delegate.invoke(request)
}
