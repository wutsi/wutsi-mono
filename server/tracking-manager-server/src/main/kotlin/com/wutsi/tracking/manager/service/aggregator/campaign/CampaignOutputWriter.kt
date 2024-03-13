package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class CampaignOutputWriter(path: String, storage: StorageService) : OutputWriter<CampaignKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "account_id",
            "campaign",
            "total_impressions",
        )

    override fun values(pair: KeyPair<CampaignKey, Long>): Array<Any?> =
        arrayOf(
            pair.key.accountId,
            pair.key.campaign,
            pair.value,
        )
}
