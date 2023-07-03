package com.wutsi.tracking.manager.service.aggregator.reader

import com.wutsi.tracking.manager.service.aggregator.KeyPair

class ReaderValue(key: ReaderKey, value: Long) : KeyPair<ReaderKey, Long>(key, value)
