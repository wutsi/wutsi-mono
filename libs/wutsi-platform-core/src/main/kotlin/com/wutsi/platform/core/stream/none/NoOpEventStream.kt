package com.wutsi.platform.core.stream.none

import com.wutsi.platform.core.stream.EventStream

/**
 * Implementation of {@link com.wutsi.platform.core.stream.Stream} that does nothing
 */
class NoOpEventStream : EventStream {
    override fun close() {
    }

    override fun enqueue(type: String, payload: Any) {
    }

    override fun publish(type: String, payload: Any) {
    }

    override fun subscribeTo(source: String) {
    }
}
