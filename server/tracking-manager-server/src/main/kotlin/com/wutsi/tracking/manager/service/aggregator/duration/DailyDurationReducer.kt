package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import kotlin.math.min

class DailyDurationReducer : Reducer<DurationKey, Long> {
    override fun reduce(values: List<KeyPair<DurationKey, Long>>): KeyPair<DurationKey, Long> {
        return if (values.size == 1) {
            KeyPair(
                values[0].key,
                0,
            )
        } else if (values.size == 2) {
            val start = values.minBy { it.value }
            val end = values.maxBy { it.value }
            KeyPair(
                start.key,
                min((end.value - start.value) / 1000, 60), // Not more than 1m if only 2 events
            )
        } else {
            val xvalues = removeOutliers(values.sortedBy { it.value })
            val start = xvalues[0]
            val end = xvalues[xvalues.size - 1]
            KeyPair(
                start.key,
                Math.max(end.value - start.value, 0) / 1000,
            )
        }
    }

    /**
     * Removing outlier using IQR method
     * See https://towardsdatascience.com/why-1-5-in-iqr-method-of-outlier-detection-5d07fdc82097
     */
    private fun removeOutliers(values: List<KeyPair<DurationKey, Long>>): List<KeyPair<DurationKey, Long>> {
        val data = values.map { it.value.toDouble() }.toDoubleArray()
        val ds = DescriptiveStatistics(data)
        val q1 = ds.getPercentile(25.0)
        val q3 = ds.getPercentile(75.0)
        val iqr = q3 - q1
        val min = q1 - 1.5 * iqr
        val max = q3 + 1.5 * iqr
        return values.filter { it.value >= min && it.value <= max }
    }
}
