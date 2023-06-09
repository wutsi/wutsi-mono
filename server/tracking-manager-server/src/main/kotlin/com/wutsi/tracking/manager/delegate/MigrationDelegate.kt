package com.wutsi.tracking.manager.delegate

import com.wutsi.tracking.manager.service.MigrationService
import org.springframework.stereotype.Service

@Service
class MigrationDelegate(private val service: MigrationService) {
    fun invoke() {
        service.migrate(2020)
        service.migrate(2021)
        service.migrate(2022)
    }
}
