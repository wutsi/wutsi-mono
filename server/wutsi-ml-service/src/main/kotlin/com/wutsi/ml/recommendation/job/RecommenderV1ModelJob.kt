package com.wutsi.ml.recommendation.job

import com.wutsi.ml.event.EventType
import com.wutsi.ml.matrix.Matrix
import com.wutsi.ml.recommendation.service.RecommenderV1Model
import com.wutsi.ml.recommendation.service.RecommenderV1ModelTrainer
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.util.UUID

@Service
class RecommenderV1ModelJob(
    private val storage: StorageService,
    private val logger: KVLogger,
    private val eventStream: EventStream,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecommenderV1ModelJob::class.java)
        private val LOSS_THRESHOLD = 100.0
    }

    override fun getJobName() = "recommender-v1-model"

    override fun doRun(): Long {
        val trainer = RecommenderV1ModelTrainer()

        // Initialize
        LOGGER.info(">>> Initializing")
        val fusers = FileInputStream(load("feeds/users.csv"))
        val fstories = FileInputStream(load("feeds/stories.csv"))
        val freads = FileInputStream(load("feeds/readers.csv"))
        fusers.use {
            fstories.use {
                freads.use {
                    trainer.init(fusers, fstories, freads)
                }
            }
        }

        // Train
        LOGGER.info(">>> Training model")
        val loss = trainer.train(
            features = 3,
            iterations = 5000,
            lr = 0.003,
            l2 = 0.04,
        )
        logger.add("loss", loss)

        // Store matrix
        if (loss <= LOSS_THRESHOLD) {
            LOGGER.info(">>> Storing matrices")
            store(trainer.u(), RecommenderV1Model.U_PATH)
            store(trainer.v(), RecommenderV1Model.V_PATH)

            // Notify
            eventStream.enqueue(EventType.RECOMMENDER_V1_MODEL_TRAINED, mutableMapOf<String, String>())

            return 1L
        } else {
            return 0L
        }
    }

    private fun load(path: String): File {
        LOGGER.info(">>>    Loading $path")
        val file = Files.createTempFile(path.substring(path.indexOf("/") + 1), ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            storage.get(storage.toURL(path), fout)
        }
        return file
    }

    private fun store(m: Matrix, path: String): URL {
        LOGGER.info(">>>    Storing $path")

        // Store locally
        val file = Files.createTempFile(UUID.randomUUID().toString(), ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            m.save(fout)
        }

        // Store on the cloud
        val fin = FileInputStream(file)
        return fin.use {
            storage.store(path, fin, "text/csv", contentLength = file.length())
        }
    }
}
