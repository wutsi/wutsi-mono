package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class MaxScrollReducer : Reducer<ScrollKey, Long> {
    override fun reduce(values: List<KeyPair<ScrollKey, Long>>): KeyPair<ScrollKey, Long> {
        return KeyPair(
            key = ScrollKey(extractProductId(values[0].key.productId)),
            value = values.maxBy { it.value }.value,
        )
    }

    private fun extractProductId(key: String) = key.split("|")[1]
}
