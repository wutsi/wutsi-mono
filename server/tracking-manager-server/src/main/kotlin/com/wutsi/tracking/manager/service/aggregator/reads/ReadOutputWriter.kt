package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class ReadOutputWriter(path: String, storage: StorageService) : OutputWriter<ReadKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "product_id",
            "total_reads",
        )

    override fun values(pair: KeyPair<ReadKey, Long>): Array<Any> =
        arrayOf(
            pair.key.productId,
            pair.value,
        )
}
