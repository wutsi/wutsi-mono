package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ClickReducer : Reducer<ClickKey, Long> {
    override fun reduce(values: List<KeyPair<ClickKey, Long>>): KeyPair<ClickKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
