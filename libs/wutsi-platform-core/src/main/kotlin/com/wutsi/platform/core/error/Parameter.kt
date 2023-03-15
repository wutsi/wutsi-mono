package com.wutsi.platform.core.error

data class Parameter(
    val name: String = "",
    val type: ParameterType? = null,
    val value: Any? = null,
)
