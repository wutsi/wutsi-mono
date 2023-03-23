package com.wutsi.marketplace.manager.util.csv

data class CsvError(
    val row: Int,
    val column: Int? = null,
    val code: String? = null,
    val description: String? = null,
)
