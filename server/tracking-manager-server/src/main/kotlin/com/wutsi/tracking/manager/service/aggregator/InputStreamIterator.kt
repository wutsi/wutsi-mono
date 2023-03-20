package com.wutsi.tracking.manager.service.aggregator

import java.io.InputStream

interface InputStreamIterator {
    fun next(): InputStream
    fun hasNext(): Boolean
}
