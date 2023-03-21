package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.SearchPictureDelegate
import com.wutsi.marketplace.access.dto.SearchPictureRequest
import com.wutsi.marketplace.access.dto.SearchPictureResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchPictureController(
    public val `delegate`: SearchPictureDelegate,
) {
    @PostMapping("/v1/pictures/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchPictureRequest,
    ): SearchPictureResponse =
        delegate.invoke(request)
}
