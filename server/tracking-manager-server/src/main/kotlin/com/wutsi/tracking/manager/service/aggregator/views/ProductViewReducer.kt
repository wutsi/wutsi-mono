package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ProductViewReducer : Reducer<ProductKey, Long> {
    override fun reduce(acc: KeyPair<ProductKey, Long>, cur: KeyPair<ProductKey, Long>): KeyPair<ProductKey, Long> =
        KeyPair(acc.key, acc.value + cur.value)
}
