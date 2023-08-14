package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class SourceReducer : Reducer<SourceKey, Long> {
    override fun reduce(values: List<KeyPair<SourceKey, Long>>): KeyPair<SourceKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
