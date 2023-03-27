package com.wutsi.application.util

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

object DateTimeUtil {
    fun convert(dateTime: OffsetDateTime, timezoneId: String?): OffsetDateTime =
        if (timezoneId == null) {
            dateTime
        } else {
            dateTime.atZoneSameInstant(ZoneId.of(timezoneId)).toOffsetDateTime()
        }

    fun convert(date: LocalDate, timezoneId: String?): LocalDate =
        if (timezoneId == null) {
            date
        } else {
            date.atStartOfDay(ZoneId.of(timezoneId)).toLocalDate()
        }
}
