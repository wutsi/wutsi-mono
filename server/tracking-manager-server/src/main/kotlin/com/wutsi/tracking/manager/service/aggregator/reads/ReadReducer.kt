package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ReadReducer : Reducer<ReadKey, Long> {
    override fun reduce(values: List<KeyPair<ReadKey, Long>>): KeyPair<ReadKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
