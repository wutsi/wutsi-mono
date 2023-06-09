package com.wutsi.tracking.manager

import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

interface Repository<I> {
    fun read(input: InputStream): List<I>
    fun save(
        items: List<I>,
        date: LocalDate = LocalDate.now(ZoneId.of("UTC")),
        filename: String = "${UUID.randomUUID()}.csv",
    ): URL

    fun getURLs(date: LocalDate): List<URL>
}
