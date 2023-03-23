package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.SearchMeetingProviderDelegate
import com.wutsi.marketplace.manager.dto.SearchMeetingProviderResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class SearchMeetingProviderController(
    public val `delegate`: SearchMeetingProviderDelegate,
) {
    @PostMapping("/v1/meeting-providers/search")
    public fun invoke(): SearchMeetingProviderResponse = delegate.invoke()
}
