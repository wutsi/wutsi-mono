package com.wutsi.tracking.manager.service.aggregator.reader

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class ReaderOutputWriter(path: String, storage: StorageService) : OutputWriter<ReaderKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "account_id",
            "device_id",
            "product_id",
            "total_reads",
        )

    override fun values(pair: KeyPair<ReaderKey, Long>): Array<Any?> =
        arrayOf(
            pair.key.accountId,
            pair.key.deviceId,
            pair.key.productId,
            pair.value,
        )
}
