package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.tracking.manager.dao.TrackRepository
import org.slf4j.LoggerFactory
import java.io.InputStream

class Aggregator<K, V>(
    private val dao: TrackRepository,
    private val inputs: InputStreamIterator,
    private val mapper: Mapper<K, V>,
    private val reducer: Reducer<K, V>,
    private val output: OutputWriter<K, V>,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(Aggregator::class.java)
    }

    fun aggregate() {
        // Map
        val keyPairs = mutableListOf<KeyPair<K, V>>()
        while (inputs.hasNext()) {
            keyPairs.addAll(
                map(inputs.next()).filterNotNull(),
            )
        }
        LOGGER.info("Mapper: ${keyPairs.size} input(s)")

        // Reduce
        val groups = keyPairs.groupBy { it.key }
        val results = groups.map { reduce(it.value) }
        LOGGER.info("Reducer: ${results.size} output(s)")

        // Output
        output.write(results)
    }

    private fun map(input: InputStream): List<KeyPair<K, V>?> =
        dao.read(input, mapper).map { mapper.map(it) }

    private fun reduce(group: List<KeyPair<K, V>>): KeyPair<K, V> =
        group.reduce { acc, keyPair -> reducer.reduce(acc, keyPair) }
}
