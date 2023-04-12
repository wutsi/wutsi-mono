package com.wutsi.checkout.access.endpoint

import com.wutsi.checkout.access.`delegate`.SearchDonationKpiDelegate
import com.wutsi.checkout.access.dto.SearchDonationKpiRequest
import com.wutsi.checkout.access.dto.SearchDonationKpiResponse
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
    ):
        SearchDonationKpiResponse = delegate.invoke(request)
}
