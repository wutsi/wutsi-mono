package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CreateFileDelegate
import com.wutsi.marketplace.access.dto.CreateFileRequest
import com.wutsi.marketplace.access.dto.CreateFileResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateFileController(
    public val `delegate`: CreateFileDelegate,
) {
    @PostMapping("/v1/files")
    public fun invoke(
        @Valid @RequestBody
        request: CreateFileRequest,
    ): CreateFileResponse =
        delegate.invoke(request)
}
