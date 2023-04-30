package com.wutsi.checkout.manager.endpoint

import com.wutsi.checkout.manager.`delegate`.SearchDonationKpiDelegate
import com.wutsi.checkout.manager.dto.SearchDonationKpiRequest
import com.wutsi.checkout.manager.dto.SearchDonationKpiResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchDonationKpiController(
    public val `delegate`: SearchDonationKpiDelegate,
) {
    @PostMapping("/v1/kpis/donations/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchDonationKpiRequest,
    ): SearchDonationKpiResponse = delegate.invoke(request)
}
