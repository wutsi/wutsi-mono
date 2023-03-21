package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.SearchPlaceDelegate
import com.wutsi.membership.access.dto.SearchPlaceRequest
import com.wutsi.membership.access.dto.SearchPlaceResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchPlaceController(
    public val `delegate`: SearchPlaceDelegate,
) {
    @PostMapping("/v1/places/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchPlaceRequest,
    ): SearchPlaceResponse =
        delegate.invoke(request)
}
