package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class FromOutputWriter(path: String, storage: StorageService) : OutputWriter<FromKey, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "from",
            "total_reads",
        )

    override fun values(pair: KeyPair<FromKey, Long>): Array<Any?> =
        arrayOf(
            pair.key.from,
            pair.value,
        )
}
