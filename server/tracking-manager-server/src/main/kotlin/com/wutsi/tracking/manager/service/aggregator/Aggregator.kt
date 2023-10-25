package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.tracking.manager.Repository

class Aggregator<I, K, V>(
    private val dao: Repository<I>,
    private val inputs: InputStreamIterator,
    private val mapper: Mapper<I, K, V>,
    private val reducer: Reducer<K, V>,
    private val output: OutputWriter<K, V>,
    private val filter: Filter<I>? = null,
) {
    fun aggregate(): Int {
        // Map
        val keyPairs = mutableListOf<KeyPair<K, V>>()
        while (inputs.hasNext()) {
            val result = dao.read(inputs.next())
                .filter { filter == null || filter.accept(it) }
                .flatMap { mapper.map(it) }
            keyPairs.addAll(result)
        }

        // Reduce
        val groups = keyPairs.groupBy { it.key }
        val results = groups.map {
            reducer.reduce(it.value)
        }

        // Output
        output.write(results)

        return results.size
    }
}
