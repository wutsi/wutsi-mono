package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.tracking.manager.service.aggregator.KeyPair

class SourceValue(key: SourceKey, value: Long) : KeyPair<SourceKey, Long>(key, value)
