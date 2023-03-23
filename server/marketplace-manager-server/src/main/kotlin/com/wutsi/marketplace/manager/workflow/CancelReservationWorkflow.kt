package com.wutsi.marketplace.manager.workflow

import com.wutsi.enums.ReservationStatus
import com.wutsi.marketplace.access.dto.UpdateReservationStatusRequest
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class CancelReservationWorkflow(
    eventStream: EventStream,
) : AbstractMarketplaceWorkflow<Long, Unit, Void>(eventStream) {
    override fun getEventType(reservationId: Long, response: Unit, context: WorkflowContext): String? = null

    override fun getValidationRules(reservationId: Long, context: WorkflowContext): RuleSet = RuleSet.NONE

    override fun toEventPayload(reservationId: Long, response: Unit, context: WorkflowContext): Void? = null

    override fun doExecute(reservationId: Long, context: WorkflowContext) {
        marketplaceAccessApi.updateReservationStatus(
            id = reservationId,
            request = UpdateReservationStatusRequest(
                status = ReservationStatus.CANCELLED.name,
            ),
        )
    }
}
