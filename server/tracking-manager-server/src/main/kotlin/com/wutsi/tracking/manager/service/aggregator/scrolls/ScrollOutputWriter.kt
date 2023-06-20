package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class ScrollOutputWriter(path: String, storage: StorageService) : OutputWriter<ScrollKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "product_id",
            "average_scrolls",
        )

    override fun values(pair: KeyPair<ScrollKey, Long>): Array<Any> =
        arrayOf(
            pair.key.productId,
            pair.value,
        )
}
