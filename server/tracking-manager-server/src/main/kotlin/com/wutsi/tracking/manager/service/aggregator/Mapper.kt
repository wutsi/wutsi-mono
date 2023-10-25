package com.wutsi.tracking.manager.service.aggregator

interface Mapper<I, K, V> {
    fun map(input: I): List<KeyPair<K, V>>
}
