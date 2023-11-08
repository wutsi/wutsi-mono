package com.wutsi.blog.kpi.service

import java.time.LocalDate

interface KpiImporter {
    fun import(date: LocalDate): Long
}
