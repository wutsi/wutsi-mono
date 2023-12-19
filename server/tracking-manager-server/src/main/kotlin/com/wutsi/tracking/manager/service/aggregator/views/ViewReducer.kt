package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ViewReducer : Reducer<ViewKey, Long> {
    override fun reduce(values: List<KeyPair<ViewKey, Long>>): KeyPair<ViewKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
