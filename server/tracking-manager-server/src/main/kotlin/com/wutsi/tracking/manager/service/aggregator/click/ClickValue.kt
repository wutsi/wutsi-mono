package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.service.aggregator.KeyPair

class ClickValue(key: ClickKey, value: Long) : KeyPair<ClickKey, Long>(key, value)
