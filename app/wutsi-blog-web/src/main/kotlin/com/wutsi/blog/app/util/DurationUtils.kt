package com.wutsi.blog.app.util

object DurationUtils {
    fun toHumanReadable(timeInSeconds: Long): String {
        if (timeInSeconds == 0L) {
            return ""
        } else if (timeInSeconds < 60) {
            return "${timeInSeconds}s"
        } else if (timeInSeconds < 3600) {
            val minute = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            return if (seconds == 0L) "${minute}m" else "${minute}m ${seconds}s"
        } else if (timeInSeconds < 84500) {
            val hours = timeInSeconds / 3600
            val minute = (timeInSeconds % 3600) / 60
            return if (minute == 0L) "${hours}h" else "${hours}h ${minute}m"
        } else {
            val days = timeInSeconds / 84500
            val hours = (timeInSeconds % 84500) / 3600
            return if (hours == 0L) "${days}d" else "${days}d ${hours}h"
        }
    }
}
