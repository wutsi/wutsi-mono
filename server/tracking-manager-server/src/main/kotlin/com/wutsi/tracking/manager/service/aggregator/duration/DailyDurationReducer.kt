package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class DailyDurationReducer : Reducer<DurationKey, Long> {
    override fun reduce(values: List<KeyPair<DurationKey, Long>>): KeyPair<DurationKey, Long> =
        if (values.size == 1) {
            KeyPair(
                values[0].key,
                0,
            )
        } else {
            val start = values.minBy { it.value }
            val end = values.maxBy { it.value }
            KeyPair(
                start.key,
                Math.max(end.value - start.value, 0) / 1000,
            )
        }
}
