package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.CreatePictureDelegate
import com.wutsi.marketplace.manager.dto.CreatePictureRequest
import com.wutsi.marketplace.manager.dto.CreatePictureResponse
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
