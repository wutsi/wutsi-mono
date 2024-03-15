package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.entity.CampaignClickEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyCampaignClickMapper : Mapper<CampaignClickEntity, CampaignKey, Long> {
    override fun map(campaign: CampaignClickEntity): List<KeyPair<CampaignKey, Long>> =
        listOf(
            CampaignValue(
                CampaignKey(campaign.campaign),
                campaign.totalClicks,
            )
        )
}
