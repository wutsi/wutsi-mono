package com.wutsi.tracking.manager.service.aggregator

interface Mapper<I, K, V> {
    fun map(input: I): KeyPair<K, V>
}
