package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.service.aggregator.KeyPair

class Read(key: ReadKey, value: Long) : KeyPair<ReadKey, Long>(key, value)
