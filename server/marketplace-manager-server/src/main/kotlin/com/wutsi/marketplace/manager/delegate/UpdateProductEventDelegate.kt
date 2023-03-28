package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.manager.dto.UpdateProductEventRequest
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class UpdateProductEventDelegate(
    private val logger: KVLogger,
) : AbstractUpdateProductDelegate() {
    public fun invoke(request: UpdateProductEventRequest) {
        logger.add("request_online", request.online)
        logger.add("request_meeting_id", request.meetingId)
        logger.add("request_meeting_password", request.meetingPassword)
        logger.add("request_meeting_provider_id", request.meetingProviderId)
        logger.add("request_starts", request.starts)
        logger.add("request_end", request.ends)

        val product = findProduct(request.productId)
        val account = findAccount(SecurityUtil.getAccountId())
        validate(product, account)
        update(request)
    }

    private fun update(request: UpdateProductEventRequest) {
        marketplaceAccessApi.updateProductEvent(
            id = request.productId,
            request = com.wutsi.marketplace.access.dto.UpdateProductEventRequest(
                meetingId = request.meetingId,
                meetingPassword = request.meetingPassword,
                meetingProviderId = request.meetingProviderId,
                starts = request.starts,
                ends = request.ends,
                online = request.online,
            ),
        )
    }
}
