package com.wutsi.blog.kpi.service.importer

import com.sun.org.slf4j.internal.LoggerFactory
import com.wutsi.blog.kpi.service.KpiImporter
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KpiImporterSet(
    private val clickImporter: ClickKpiImporter,
    private val clickCountImport: ClickCountKpiImporter,
    private val readerImporter: ReaderKpiImporter,
    private val readerCouterKpiImporter: ReaderCountKpiImporter,
    private val durationImporter: DurationKpiImporter,
    private val sourceImporter: SourceImporter,
    private val subscriptionImporter: SubscriptionKpiImporter,
    private val readImporter: ReadKpiImporter,
    private val emailImporter: EmailKpiImporter,
    private val counterUpdater: CounterKpiUpdater,
    private val clickRateKpiImporter: ClickRateKpiImporter,
    private val likeImporter: LikeKpiImporter,
    private val commentImporter: CommentKpiImporter,
    private val userKpiImporter: UserKpiImporter,
    private val userBlogKpiImporter: UserBlogKpiImporter,
    private val userWPPKpiImporter: UserWPPKpiImporter,
    private val storeKpiImporter: StoreKpiImporter,
    private val publicationKpiImporter: PublicationKpiImporter,
    private val productKpiImporter: ProductKpiImporter,
    private val donationKpiImporter: DonationKpiImporter,
    private val donationValueKpiImporter: DonationValueKpiImporter,
    private val salesKpiImporter: SalesKpiImporter,
    private val salesValueKpiImporter: SalesValueKpiImporter,
    private val viewKpiImporter: ViewKpiImporter,
    private val adsImpressionKpiImporter: AdsImpressionKpiImporter,
    private val adsClickKpiImporter: AdsClickKpiImporter,
    private val adsImpressionTodayKpiImporter: AdsImpressionTodayKpiImporter,
    private val transactionKpiImporter: TransactionKpiImporter,
    private val transactionSuccessKpiImporter: TransactionSuccessKpiImporter,
    private val transactionRateKpiImporter: TransactionRateKpiImporter,
) : KpiImporter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiImporterSet::class.java)
    }

    private val importers = listOf(
        sourceImporter,
        durationImporter,
        clickImporter,
        clickCountImport,
        readerImporter,
        readerCouterKpiImporter,
        clickRateKpiImporter, // IMPORT: MUST be after click and reader importers
        readImporter,
        emailImporter,
        likeImporter,
        commentImporter,

        userKpiImporter,
        userBlogKpiImporter,
        userWPPKpiImporter,
        storeKpiImporter,
        publicationKpiImporter,
        productKpiImporter,
        donationKpiImporter,
        donationValueKpiImporter,
        salesKpiImporter,
        salesValueKpiImporter,
        viewKpiImporter,

        adsClickKpiImporter,
        adsImpressionKpiImporter,
        adsImpressionTodayKpiImporter,

        transactionKpiImporter,
        transactionSuccessKpiImporter,
        transactionRateKpiImporter,

        subscriptionImporter,
        counterUpdater, // IMPORTANT: MUST be last
    )

    override fun import(date: LocalDate): Long {
        var result = 0L
        importers.forEach { importer ->
            try {
                result += importer.import(date)
            } catch (ex: Exception) {
                LOGGER.warn("Unexpected error", ex)
            }
        }
        return result
    }
}
