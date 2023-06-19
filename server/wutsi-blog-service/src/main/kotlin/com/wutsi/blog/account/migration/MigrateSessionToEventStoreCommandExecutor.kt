package com.wutsi.blog.account.migration

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.domain.SessionEntity
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth/commands/migrate-to-event-stream")
class MigrateToEventStoreCommandExecutor(
    private val dao: SessionRepository,
    private val migrator: LoginMigrator,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<SessionEntity>(logger) {
    @Async
    @GetMapping
    override fun execute() {
        super.execute()
    }

    override fun getItemsToMigrate(): List<SessionEntity> =
        dao.findAll().toList()

    override fun migrate(item: SessionEntity) {
        migrator.migrate(item)
    }
}
