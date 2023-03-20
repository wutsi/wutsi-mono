package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.GetKeyDelegate
import com.wutsi.security.manager.dto.GetKeyResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
public class GetKeyController(
    public val delegate: GetKeyDelegate,
) {
    @GetMapping("/v1/keys/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetKeyResponse = delegate.invoke(id)
}
