package com.wutsi.workflow.engine

import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import org.slf4j.LoggerFactory

open class WorkflowEngine(
    private val eventStream: EventStream,
) {
    companion object {
        const val EVENT_EXECUTE = "urn:wutsi:event:workflow:execute"

        private val LOGGER = LoggerFactory.getLogger(WorkflowEngine::class.java)
    }

    private val workflows = mutableMapOf<String, Workflow>()

    open fun register(id: String, workflow: Workflow) {
        LOGGER.info("Registering Workflow: $id ${workflow.javaClass.name}")
        if (workflows.containsKey(id)) {
            throw IllegalStateException("$id already registered")
        }
        workflows[id] = workflow
    }

    open fun execute(id: String, context: WorkflowContext) {
        val logger = DefaultKVLogger()
        try {
            logger.add("workflow_id", id)
            logger.add("async", false)
            logger.add("context", context.toString())
            workflows[id]?.execute(context)
        } finally {
            logger.log()
        }
    }

    open fun executeAsync(id: String, context: WorkflowContext) {
        val logger = DefaultKVLogger()
        try {
            logger.add("workflow_id", id)
            logger.add("async", true)
            logger.add("context", context.toString())

            eventStream.enqueue(
                type = EVENT_EXECUTE,
                payload = WorkflowEventPayload(
                    id = id,
                    context = context,
                ),
            )
        } finally {
            logger.log()
        }
    }
}
