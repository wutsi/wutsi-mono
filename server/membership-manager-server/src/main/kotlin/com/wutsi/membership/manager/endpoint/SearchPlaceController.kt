package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.SearchPlaceDelegate
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.membership.manager.dto.SearchPlaceResponse
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
