package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class DurationOutputWriter(path: String, storage: StorageService) :
    OutputWriter<DurationKey, DurationData>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "correlation_id",
            "product_id",
            "total_seconds",
        )

    override fun values(pair: KeyPair<DurationKey, DurationData>): Array<Any?> =
        arrayOf(
            pair.key.correlationId,
            pair.key.productId,
            pair.value.value,
        )
}
