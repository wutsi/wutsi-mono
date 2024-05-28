package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.MailService
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Deprecated("Disable this service, its doesn't seem to drive any sales")
@Service
class EBookLaunchEmailJob(
    private val productService: ProductService,
    private val logger: KVLogger,
    private val clock: Clock,
    private val mailService: MailService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EBookLaunchEmailJob::class.java)
    }

    override fun getJobName() = "ebook-launch"

    /**
     * Disable this service, its doesn't seem to drive any sales
     * @Scheduled(cron = "\${wutsi.crontab.ebook-launch}")
     */
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val today = DateUtils.toLocalDate(Date(clock.millis()))
        val yesterday = DateUtils.toDate(today.minusDays(1))
        logger.add("date", yesterday)

        val products = productService.searchProducts(
            SearchProductRequest(
                type = ProductType.EBOOK,
                publishedStartDate = yesterday,
                status = ProductStatus.PUBLISHED,
                available = true,
                limit = 100,
            ),
        )
        logger.add("product_count", products.size)

        products.forEach { product ->
            try {
                mailService.sendEBookLaunch(product)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send the daily email for Story#${product.id}", ex)
            }
        }
        return products.size.toLong()
    }
}
