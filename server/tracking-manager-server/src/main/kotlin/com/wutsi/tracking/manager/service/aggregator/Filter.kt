package com.wutsi.tracking.manager.service.aggregator

interface Filter<I> {
    fun accept(entity: I): Boolean
}
