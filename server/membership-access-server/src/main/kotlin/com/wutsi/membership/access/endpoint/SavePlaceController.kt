package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.SavePlaceDelegate
import com.wutsi.membership.access.dto.SavePlaceRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SavePlaceController(
    public val `delegate`: SavePlaceDelegate,
) {
    @PostMapping("/v1/places")
    public fun invoke(
        @Valid @RequestBody
        request: SavePlaceRequest,
    ) {
        delegate.invoke(request)
    }
}
