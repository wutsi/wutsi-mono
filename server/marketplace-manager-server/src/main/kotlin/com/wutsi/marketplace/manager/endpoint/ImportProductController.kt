package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.ImportProductDelegate
import com.wutsi.marketplace.manager.dto.ImportProductRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class ImportProductController(
    public val `delegate`: ImportProductDelegate,
) {
    @PostMapping("/v1/products/import")
    public fun invoke(
        @Valid @RequestBody
        request: ImportProductRequest,
    ) {
        delegate.invoke(request)
    }
}
