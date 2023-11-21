package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import kotlin.math.max
import kotlin.math.min

class DailyDurationReducer : Reducer<DurationKey, DurationData> {
	companion object {
		const val MAX_DURATION_SECONDS = 5 * 60L // 5 minutes
	}

	override fun reduce(values: List<KeyPair<DurationKey, DurationData>>): KeyPair<DurationKey, DurationData> {
		val result = if (!hasStart(values)) {
			KeyPair(
				values[0].key,
				DurationData(values[0].value.event, 0L),
			)
		} else {
			val start = values.minBy { it.value.value }
			val end = values.maxBy { it.value.value }
			KeyPair(
				start.key,
				DurationData(
					event = "-",
					value = (end.value.value - start.value.value) / 1000L
				),
			)
		}

		if (result.value.value > MAX_DURATION_SECONDS) {
			val xvalues = removeOutliers(values.sortedBy { it.value.value })
			val start = xvalues[0]
			val end = xvalues[xvalues.size - 1]
			return KeyPair(
				start.key,
				DurationData(
					event = "-",
					value = min(
						MAX_DURATION_SECONDS,
						max(end.value.value - start.value.value, 0) / 1000
					),
				)
			)
		}
		return result
	}

	private fun hasStart(values: List<KeyPair<DurationKey, DurationData>>): Boolean =
		values.find { it.value.event == DailyDurationFilter.EVENT_START } != null

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
