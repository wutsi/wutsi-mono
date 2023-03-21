package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CreatePictureDelegate
import com.wutsi.marketplace.access.dto.CreatePictureRequest
import com.wutsi.marketplace.access.dto.CreatePictureResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreatePictureController(
    public val `delegate`: CreatePictureDelegate,
) {
    @PostMapping("/v1/pictures")
    public fun invoke(
        @Valid @RequestBody
        request: CreatePictureRequest,
    ): CreatePictureResponse =
        delegate.invoke(request)
}
