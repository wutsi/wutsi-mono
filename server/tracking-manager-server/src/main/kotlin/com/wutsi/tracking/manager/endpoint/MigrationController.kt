package com.wutsi.tracking.manager.endpoint

import com.wutsi.tracking.manager.delegate.MigrationDelegate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MigrationController(
    val `delegate`: MigrationDelegate,
) {
    @GetMapping("/v1/tracks/migrate")
    public fun invoke() {
        delegate.invoke()
    }
}
