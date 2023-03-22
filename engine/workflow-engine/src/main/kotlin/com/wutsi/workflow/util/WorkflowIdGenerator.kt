package com.wutsi.workflow.util

object WorkflowIdGenerator {
    fun generate(domain: String, name: String) = "urn:wutsi:workflow:$domain:$name"
}
