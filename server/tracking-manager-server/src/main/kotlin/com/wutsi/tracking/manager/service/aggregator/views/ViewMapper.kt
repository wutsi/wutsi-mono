package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class ViewMapper : Mapper<ViewKey, Long> {
    companion object {
        const val EVENT = "load"
        const val PAGE = "page.web.product"
    }

    override fun map(track: TrackEntity): KeyPair<ViewKey, Long>? =
        track.productId?.let {
            View(
                ViewKey(track.businessId ?: "-1", track.productId),
                1,
            )
        }
}
