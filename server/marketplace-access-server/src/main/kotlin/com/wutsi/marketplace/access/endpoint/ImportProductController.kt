package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.ImportProductDelegate
import com.wutsi.marketplace.access.dto.ImportProductRequest
import com.wutsi.marketplace.access.dto.ImportProductResponse
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
    ): ImportProductResponse =
        delegate.invoke(request)
}
