package com.wutsi.security.manager.delegate

import com.wutsi.security.manager.dto.CreatePasswordRequest
import com.wutsi.security.manager.dto.CreatePasswordResponse
import com.wutsi.security.manager.service.PasswordService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreatePasswordDelegate(
    private val service: PasswordService,
) {
    @Transactional
    public fun invoke(request: CreatePasswordRequest) =
        CreatePasswordResponse(
            passwordId = service.create(request).id!!,
        )
}
