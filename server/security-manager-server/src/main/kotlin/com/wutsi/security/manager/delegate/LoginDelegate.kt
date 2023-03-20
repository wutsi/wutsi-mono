package com.wutsi.security.manager.delegate

import com.wutsi.security.manager.dto.LoginRequest
import com.wutsi.security.manager.dto.LoginResponse
import org.springframework.stereotype.Service

@Service
public class LoginDelegate() {
    public fun invoke(request: LoginRequest): LoginResponse {
        TODO()
    }
}
