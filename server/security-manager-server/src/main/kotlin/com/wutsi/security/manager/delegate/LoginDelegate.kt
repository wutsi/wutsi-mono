package com.wutsi.security.manager.delegate

import com.wutsi.security.manager.dto.LoginRequest
import com.wutsi.security.manager.dto.LoginResponse
import com.wutsi.security.manager.service.LoginService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class LoginDelegate(private val service: LoginService) {
    @Transactional
    public fun invoke(request: LoginRequest): LoginResponse =
        LoginResponse(
            accessToken = service.login(request),
        )
}
