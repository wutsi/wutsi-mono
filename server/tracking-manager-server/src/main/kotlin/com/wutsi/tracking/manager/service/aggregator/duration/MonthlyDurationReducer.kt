package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class MonthlyDurationReducer : Reducer<DurationKey, DurationData> {
    override fun reduce(values: List<KeyPair<DurationKey, DurationData>>): KeyPair<DurationKey, DurationData> =
        KeyPair(
            values[0].key,
            DurationData("-", values.sumOf { it.value.value }),
        )
}
