package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class SourceOutputWriter(path: String, storage: StorageService) : OutputWriter<SourceKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "product_id",
            "source",
            "total_reads",
        )

    override fun values(pair: KeyPair<SourceKey, Long>): Array<Any?> =
        arrayOf(
            pair.key.productId,
            pair.key.source,
            pair.value,
        )
}
