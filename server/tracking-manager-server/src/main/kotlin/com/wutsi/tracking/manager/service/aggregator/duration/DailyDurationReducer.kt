package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import kotlin.math.max
import kotlin.math.min

class DailyDurationReducer : Reducer<DurationKey, DurationData> {
    override fun reduce(values: List<KeyPair<DurationKey, DurationData>>): KeyPair<DurationKey, DurationData> {
        return if (!hasStartEnd(values)) {
            KeyPair(
                values[0].key,
                DurationData(values[0].value.event, 0L),
            )
        } else if (values.size == 2) {
            val start = values.minBy { it.value.value }
            val end = values.maxBy { it.value.value }
            KeyPair(
                start.key,
                DurationData(
                    event = "-",
                    value = min(
                        (end.value.value - start.value.value) / 1000L,
                        60L
                    ), // Not more than 1m if only 2 events
                )
            )
        } else {
            val xvalues = removeOutliers(values.sortedBy { it.value.value })
            val start = xvalues[0]
            val end = xvalues[xvalues.size - 1]
            KeyPair(
                start.key,
                DurationData(
                    event = "-",
                    value = max(end.value.value - start.value.value, 0) / 1000,
                )
            )
        }
    }

    private fun hasStartEnd(values: List<KeyPair<DurationKey, DurationData>>): Boolean {
        val start = values.find { it.value.event == DailyDurationFilter.EVENT_START }
        val end = values.find { it.value.event == DailyDurationFilter.EVENT_END }
        return start != null && end != null
    }

    /**
     * Removing outlier using IQR method
     * See https://towardsdatascience.com/why-1-5-in-iqr-method-of-outlier-detection-5d07fdc82097
     */
    private fun removeOutliers(values: List<KeyPair<DurationKey, DurationData>>): List<KeyPair<DurationKey, DurationData>> {
        val data = values.map { it.value.value.toDouble() }.toDoubleArray()
        val ds = DescriptiveStatistics(data)
        val q1 = ds.getPercentile(25.0)
        val q3 = ds.getPercentile(75.0)
        val iqr = q3 - q1
        val min = q1 - 1.5 * iqr
        val max = q3 + 1.5 * iqr
        return values.filter { it.value.value >= min && it.value.value <= max }
    }
}
