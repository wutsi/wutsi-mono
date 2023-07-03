package com.wutsi.tracking.manager.service.aggregator.reader

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ReaderReducer : Reducer<ReaderKey, Long> {
    override fun reduce(values: List<KeyPair<ReaderKey, Long>>): KeyPair<ReaderKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
