package com.wutsi.tracking.manager.service.aggregator

interface Reducer<K, V> {
    fun reduce(values: List<KeyPair<K, V>>): KeyPair<K, V>
}
