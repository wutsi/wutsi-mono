package com.wutsi.blog.kpi.service.importer

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
    private val counterUpdater: CouterKpiUpdater,
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
) : KpiImporter {
    override fun import(date: LocalDate): Long =
        sourceImporter.import(date) +
            durationImporter.import(date) +
            clickImporter.import(date) +
            clickCountImport.import(date) +
            readerImporter.import(date) +
            readerCouterKpiImporter.import(date) +
            clickRateKpiImporter.import(date) + // IMPORT: MUST be after click and reader importers
            readImporter.import(date) +
            emailImporter.import(date) +
            likeImporter.import(date) +
            commentImporter.import(date) +

            userKpiImporter.import(date) +
            userBlogKpiImporter.import(date) +
            userWPPKpiImporter.import(date) +
            storeKpiImporter.import(date) +
            publicationKpiImporter.import(date) +
            productKpiImporter.import(date) +
            donationKpiImporter.import(date) +
            donationValueKpiImporter.import(date) +
            salesKpiImporter.import(date) +
            salesValueKpiImporter.import(date) +

            subscriptionImporter.import(date) +
            counterUpdater.import(date) // IMPORTANT: MUST be last
}
