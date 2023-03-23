package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.CreateFileDelegate
import com.wutsi.marketplace.manager.dto.CreateFileRequest
import com.wutsi.marketplace.manager.dto.CreateFileResponse
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
