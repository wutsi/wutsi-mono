package com.wutsi.tracking.manager.service.aggregator

interface Reducer<K, V> {
    fun reduce(acc: KeyPair<K, V>, cur: KeyPair<K, V>): KeyPair<K, V>
}
