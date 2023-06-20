package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class AvgScrollReducer : Reducer<ScrollKey, Long> {
    override fun reduce(values: List<KeyPair<ScrollKey, Long>>): KeyPair<ScrollKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value } / values.size,
        )
}
