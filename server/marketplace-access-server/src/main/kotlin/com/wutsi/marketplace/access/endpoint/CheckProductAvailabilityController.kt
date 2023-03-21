package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CheckProductAvailabilityDelegate
import com.wutsi.marketplace.access.dto.CheckProductAvailabilityRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CheckProductAvailabilityController(
    public val `delegate`: CheckProductAvailabilityDelegate,
) {
    @PostMapping("/v1/products/availability")
    public fun invoke(
        @Valid @RequestBody
        request: CheckProductAvailabilityRequest,
    ) {
        delegate.invoke(request)
    }
}
