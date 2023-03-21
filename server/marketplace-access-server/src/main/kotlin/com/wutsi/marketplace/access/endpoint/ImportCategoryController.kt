package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.ImportCategoryDelegate
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class ImportCategoryController(
    public val `delegate`: ImportCategoryDelegate,
) {
    @PostMapping("/v1/categories/import")
    public fun invoke() {
        delegate.invoke()
    }
}
