package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class EmailReducer : Reducer<EmailKey, Long> {
    override fun reduce(values: List<KeyPair<EmailKey, Long>>): KeyPair<EmailKey, Long> =
        KeyPair(
            key = values[0].key,
            value = values.sumOf { it.value },
        )
}
