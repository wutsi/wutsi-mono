package com.wutsi.workflow

data class WorkflowContext(
    val accountId: Long? = null,
    val data: MutableMap<String, Any> = mutableMapOf(),
    val input: Any? = null,
    var output: Any? = null,
)
