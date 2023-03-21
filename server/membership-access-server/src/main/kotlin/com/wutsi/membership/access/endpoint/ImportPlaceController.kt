package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.delegate.ImportPlaceDelegate
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class ImportPlaceController(
    public val `delegate`: ImportPlaceDelegate,
) {
    @GetMapping("/v1/places/import")
    public fun invoke(@RequestParam(name = "country", required = false) country: String) {
        delegate.invoke(country)
    }
}
