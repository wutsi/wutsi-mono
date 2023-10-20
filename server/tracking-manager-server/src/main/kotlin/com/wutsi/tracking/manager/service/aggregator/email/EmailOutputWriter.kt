package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class EmailOutputWriter(path: String, storage: StorageService) : OutputWriter<EmailKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "account_id",
            "product_id",
            "total_reads",
        )

    override fun values(pair: KeyPair<EmailKey, Long>): Array<Any?> =
        arrayOf(
            pair.key.accountId,
            pair.key.productId,
            pair.value,
        )
}
