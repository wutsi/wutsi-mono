package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class MonthlyDurationReducer : Reducer<DurationKey, Long> {
    override fun reduce(values: List<KeyPair<DurationKey, Long>>): KeyPair<DurationKey, Long> =
        KeyPair(
            values[0].key,
            values.sumOf { it.value },
        )
}
