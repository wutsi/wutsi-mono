package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.LoginDelegate
import com.wutsi.security.manager.dto.LoginRequest
import com.wutsi.security.manager.dto.LoginResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class LoginController(
    public val delegate: LoginDelegate,
) {
    @PostMapping("/v1/auth")
    public fun invoke(
        @Valid @RequestBody
        request: LoginRequest,
    ): LoginResponse =
        delegate.invoke(request)
}
