package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.service.aggregator.KeyPair

class DurationValue(key: DurationKey, value: Long) : KeyPair<DurationKey, Long>(key, value)
