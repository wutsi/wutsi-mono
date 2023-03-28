package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import org.springframework.stereotype.Service

@Service
class DeletePictureDelegate(private val marketplaceAccessApi: MarketplaceAccessApi) {
    fun invoke(id: Long) {
        marketplaceAccessApi.deletePicture(id)
    }
}
