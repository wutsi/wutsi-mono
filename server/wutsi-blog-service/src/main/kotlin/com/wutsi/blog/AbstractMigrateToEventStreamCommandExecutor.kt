package com.wutsi.blog

import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping

abstract class AbstractMigrateToEventStreamCommandExecutor<T>(private val logger: KVLogger) {
    abstract fun getItemsToMigrate(): List<T>
    abstract fun migrate(item: T)

    @Async
    @GetMapping
    open fun execute() {
        val items = getItemsToMigrate()

        var migrated = 0
        var errors = 0
        items.forEach {
            try {
                migrate(it)
                migrated++
            } catch (ex: Exception) {
                LoggerFactory.getLogger(javaClass).warn("Unable to migration $it", ex)
                errors++
            } finally {
                logger.log()
            }
        }

        logger.add("migration_to_migrate", items.size)
        logger.add("migration_migrated", migrated)
        logger.add("migration_errors", errors)
    }
}
