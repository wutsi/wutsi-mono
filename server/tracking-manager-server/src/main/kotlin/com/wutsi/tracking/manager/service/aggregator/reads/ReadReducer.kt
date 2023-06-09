package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ReadReducer : Reducer<ReadKey, Long> {
    override fun reduce(acc: KeyPair<ReadKey, Long>, cur: KeyPair<ReadKey, Long>): KeyPair<ReadKey, Long> =
        KeyPair(acc.key, acc.value + cur.value)
}
