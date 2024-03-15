package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.entity.CampaignEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyCampaignMapper : Mapper<CampaignEntity, CampaignKey, Long> {
    override fun map(campaign: CampaignEntity): List<KeyPair<CampaignKey, Long>> =
        listOf(
            CampaignValue(
                CampaignKey(campaign.campaign),
                campaign.totalImpressions,
            )
        )
}
