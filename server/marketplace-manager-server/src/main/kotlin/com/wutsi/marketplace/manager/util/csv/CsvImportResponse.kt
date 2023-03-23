package com.wutsi.marketplace.manager.util.csv

data class CsvImportResponse(
    val imported: Int = 0,
    val errors: List<CsvError> = emptyList(),
)
