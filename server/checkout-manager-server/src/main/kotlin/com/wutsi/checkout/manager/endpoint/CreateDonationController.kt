package com.wutsi.checkout.manager.endpoint

import com.wutsi.checkout.manager.`delegate`.CreateDonationDelegate
import com.wutsi.checkout.manager.dto.CreateDonationRequest
import com.wutsi.checkout.manager.dto.CreateDonationResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateDonationController(
    public val `delegate`: CreateDonationDelegate,
) {
    @PostMapping("/v1/transactions/donate")
    public fun invoke(
        @Valid @RequestBody
        request: CreateDonationRequest,
    ): CreateDonationResponse =
        delegate.invoke(request)
}
