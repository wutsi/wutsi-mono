package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.tracking.manager.Repository
import org.slf4j.LoggerFactory

class Aggregator<I, K, V>(
    private val dao: Repository<I>,
    private val inputs: InputStreamIterator,
    private val mapper: Mapper<I, K, V>,
    private val reducer: Reducer<K, V>,
    private val output: OutputWriter<K, V>,
    private val filter: Filter<I>? = null,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(Aggregator::class.java)
    }

    fun aggregate() {
        // Map
        val keyPairs = mutableListOf<KeyPair<K, V>>()
        while (inputs.hasNext()) {
            val result = dao.read(inputs.next())
                .filter { filter == null || filter.accept(it) }
                .mapNotNull { mapper.map(it) }
            keyPairs.addAll(result)
        }
        LOGGER.info("Mapper: ${keyPairs.size} input(s)")

        // Reduce
        val groups = keyPairs.groupBy { it.key }
        val results = groups.map {
            it.value.reduce { acc, keyPair -> reducer.reduce(acc, keyPair) }
        }
        LOGGER.info("Reducer: ${results.size} output(s)")

        // Output
        output.write(results)
    }
}
