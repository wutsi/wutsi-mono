package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class FromReducer : Reducer<FromKey, Long> {
    override fun reduce(values: List<KeyPair<FromKey, Long>>): KeyPair<FromKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
