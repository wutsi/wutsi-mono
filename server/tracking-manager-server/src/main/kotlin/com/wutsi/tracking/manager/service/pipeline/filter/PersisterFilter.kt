package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import org.slf4j.LoggerFactory
import java.util.Collections

class PersisterFilter(
    private val dao: TrackRepository,
    private val bufferSize: Int = 10000,
) : Filter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PersisterFilter::class.java)
    }

    private val buffer = Collections.synchronizedList(mutableListOf<TrackEntity>())

    fun size(): Int = buffer.size

    fun destroy() {
        flush()
    }

    override fun filter(track: TrackEntity): TrackEntity {
        buffer.add(track)
        if (shouldFlush()) {
            flush()
        }
        return track
    }

    fun flush(): Int {
        val copy = mutableListOf<TrackEntity>()
        copy.addAll(buffer)

        if (copy.size > 0) {
            val url = dao.save(copy)
            LOGGER.info("Storing ${copy.size} tracking events(s): $url")

            buffer.removeAll(copy)
        }

        return copy.size
    }

    private fun shouldFlush(): Boolean {
        return buffer.size >= bufferSize
    }
}
