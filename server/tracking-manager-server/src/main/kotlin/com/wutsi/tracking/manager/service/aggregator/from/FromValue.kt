package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.tracking.manager.service.aggregator.KeyPair

class FromValue(key: FromKey, value: Long) : KeyPair<FromKey, Long>(key, value)
