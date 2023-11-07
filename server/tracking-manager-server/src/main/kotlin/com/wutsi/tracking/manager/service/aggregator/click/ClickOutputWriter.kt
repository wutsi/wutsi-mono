package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class ClickOutputWriter(path: String, storage: StorageService) : OutputWriter<ClickKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "product_id",
            "total_clicks",
        )

    override fun values(pair: KeyPair<ClickKey, Long>): Array<Any?> =
        arrayOf(
            pair.key.productId,
            pair.value,
        )
}
