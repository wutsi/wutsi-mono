package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.UpdatePasswordDelegate
import com.wutsi.security.manager.dto.UpdatePasswordRequest
import org.springframework.web.bind.`annotation`.PutMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class UpdatePasswordController(
    public val delegate: UpdatePasswordDelegate
) {
    @PutMapping("/v1/passwords")
    public fun invoke(
        @Valid @RequestBody
        request: UpdatePasswordRequest
    ) {
        delegate.invoke(request)
    }
}
