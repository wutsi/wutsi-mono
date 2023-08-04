package com.wutsi.ml.document.service

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream

@Service
class DocumentLoader(private val storage: StorageService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DocumentLoader::class.java)
        private val HEADERS = arrayOf(
            "id",
            "title",
            "author_id",
            "author",
            "language",
            "topic_id",
            "topic",
            "parent_topic_id",
            "parent_topic",
            "tags",
            "url",
            "summary",
            "published_date",
        )
    }

    fun load(): List<DocumentEntity> {
        val file = File.createTempFile("feeds", ".csv")
        try {
            download("feeds/stories.csv", file)
            return load(file)
        } finally {
            file.delete()
        }
    }

    private fun load(file: File): List<DocumentEntity> {
        val parser = CSVParser.parse(
            file,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*HEADERS)
                .build(),
        )

        val result = mutableListOf<DocumentEntity>()
        var row = 0
        for (record in parser) {
            row++
            try {
                val doc = toDocument(record)
                result.add(doc)
            } catch (ex: Exception) {
                LOGGER.warn("$row - Unexpected error", ex)
            }
        }
        return result
    }

    private fun toDocument(record: CSVRecord) = DocumentEntity(
        id = record.get("id")?.toLong() ?: -1,
        language = record.get("language") ?: "",
        content = listOf(
            record.get("title"),
            record.get("topic"),
            record.get("parent_topic"),
            record.get("tags")?.replace("|", ","),
            record.get("summary"),
        ).filterNotNull().joinToString("\n"),
    )

    private fun download(path: String, file: File) {
        val fout = FileOutputStream(file)
        fout.use {
            val url = storage.toURL(path)
            storage.get(url, fout)
        }
    }
}
