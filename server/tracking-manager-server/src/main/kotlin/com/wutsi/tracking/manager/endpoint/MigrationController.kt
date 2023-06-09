package com.wutsi.tracking.manager.endpoint

import com.wutsi.tracking.manager.delegate.MigrationDelegate
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class MigrationController(
    public val `delegate`: MigrationDelegate,
) {
    @PostMapping("/v1/tracks/migrate")
    public fun invoke() {
        delegate.invoke()
    }
}
