package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

open class CampaignReducer : Reducer<CampaignKey, Long> {
    override fun reduce(values: List<KeyPair<CampaignKey, Long>>): KeyPair<CampaignKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
