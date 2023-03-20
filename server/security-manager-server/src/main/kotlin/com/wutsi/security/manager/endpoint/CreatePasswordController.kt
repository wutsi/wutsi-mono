package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.CreatePasswordDelegate
import com.wutsi.security.manager.dto.CreatePasswordRequest
import com.wutsi.security.manager.dto.CreatePasswordResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreatePasswordController(
    public val delegate: CreatePasswordDelegate
) {
    @PostMapping("/v1/passwords")
    public fun invoke(
        @Valid @RequestBody
        request: CreatePasswordRequest
    ): CreatePasswordResponse =
        delegate.invoke(request)
}
