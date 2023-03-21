package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.SearchMeetingProviderDelegate
import com.wutsi.marketplace.access.dto.SearchMeetingProviderResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class SearchMeetingProviderController(
    public val `delegate`: SearchMeetingProviderDelegate,
) {
    @PostMapping("/v1/meeting-providers/search")
    public fun invoke(): SearchMeetingProviderResponse = delegate.invoke()
}
