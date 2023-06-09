package com.wutsi.tracking.manager

import java.io.InputStream
import java.net.URL
import java.time.LocalDate

interface Repository<I> {
    fun read(input: InputStream): List<I>
    fun save(items: List<I>, date: LocalDate = LocalDate.now()): URL
    fun getURLs(date: LocalDate): List<URL>
}
