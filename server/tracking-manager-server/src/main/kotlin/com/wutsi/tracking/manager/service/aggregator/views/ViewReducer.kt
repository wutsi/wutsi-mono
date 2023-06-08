package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ViewReducer : Reducer<ViewKey, Long> {
    override fun reduce(acc: KeyPair<ViewKey, Long>, cur: KeyPair<ViewKey, Long>): KeyPair<ViewKey, Long> =
        KeyPair(acc.key, acc.value + cur.value)
}
