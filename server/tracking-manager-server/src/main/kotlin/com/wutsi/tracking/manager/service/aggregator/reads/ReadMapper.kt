package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class ReadMapper : Mapper<ReadKey, Long> {
    companion object {
        const val EVENT = "load"
        const val PAGE = "page.web.product"
    }

    override fun map(track: TrackEntity): KeyPair<ReadKey, Long> =
        Read(
            ReadKey(track.productId!!),
            1,
        )
}
