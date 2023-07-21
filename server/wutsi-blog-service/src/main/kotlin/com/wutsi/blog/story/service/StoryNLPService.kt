package com.wutsi.blog.story.service

import com.wutsi.blog.nlp.service.BagOfWordExtractor
import com.wutsi.blog.nlp.service.StopWordsProvider
import com.wutsi.blog.nlp.service.Term
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import kotlin.math.log10

@Service
class StoryNLPService(
    private val bowExtractor: BagOfWordExtractor,
    private val stopWordsProvider: StopWordsProvider,
    private val storageService: StorageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryNLPService::class.java)

        private val HEADERS = arrayOf(
            "term",
            "tf",
            "idf",
        )
    }

    fun generateCorpusBagOfWord(stories: List<StoryEntity>): URL {
        // Load
        val df = mutableMapOf<String, Long>()
        stories.forEach { story ->
            try {
                val bow = loadBagOfWord(story)
                bow.forEach { term ->
                    val text = term.text
                    if (df.containsKey(text)) {
                        df[text] = df[text]!! + 1
                    } else {
                        df[text] = 1
                    }
                }
            } catch (ex: Exception) {
                LOGGER.warn("Unable to find bag-of-words for Story#${story.id}", ex)
            }
        }

        // Store
        val n = stories.size.toDouble()
        val bow = df.keys.map {
            Term(
                text = it,
                idf = log10(n / df[it]!!),
            )
        }

        val file = File.createTempFile("bag-of-words", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            toCsv(bow, fout)
            val fin = FileInputStream(file)
            fin.use {
                val path = "stories/bag-of-words.csv"
                return storageService.store(path, fin, "text/csv", null, "utf-8")
            }
        }
    }

    fun generateStoryBagOfWord(story: StoryEntity) {
        val lang = story.language ?: "en"
        val stopWords = stopWordsProvider.get(lang)
        val text = toText(story)
        val bow = bowExtractor.extract(text, stopWords)

        val file = File.createTempFile("bow", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            toCsv(bow, fout)
            val fin = FileInputStream(file)
            fin.use {
                val path = "stories/${story.id}/bag-of-words.csv"
                storageService.store(path, fin, "text/csv", null, "utf-8")

                LOGGER.info(">>> Story Bag-Of-Words for Story#${story.id}  to $path")
            }
        }
    }

    private fun loadBagOfWord(story: StoryEntity): List<Term> {
        val file = File.createTempFile("bow", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            val url = storageService.toURL("stories/${story.id}/bag-of-words.csv")
            storageService.get(url, fout)
            val fin = FileInputStream(file)
            fin.use {
                return fromCsv(fin)
            }
        }
    }

    private fun toCsv(bow: List<Term>, fout: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(fout))
        val printer = CSVPrinter(
            writer,
            CSVFormat.DEFAULT
                .builder()
                .setHeader(*HEADERS)
                .build(),
        )
        printer.use {
            bow.forEach {
                printer.printRecord(
                    it.text,
                    it.tf,
                    it.idf,
                )
            }
        }
    }

    private fun fromCsv(fin: InputStream): List<Term> {
        val result = mutableListOf<Term>()
        val parser = CSVParser.parse(
            fin,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*HEADERS)
                .build(),
        )
        parser.use {
            for (record in parser) {
                result.add(
                    Term(
                        text = record.get(0)?.trim() ?: "",
                        tf = record.get(1)?.trim()?.toDoubleOrNull(),
                        idf = record.get(2)?.trim()?.toDoubleOrNull(),
                    ),
                )
            }
            return result
        }
    }

    private fun toText(story: StoryEntity): String {
        val sb = StringBuilder()
        sb.append(story.title).append('\n')
        story.summary?.let { sb.append(it) }
        return sb.toString()
    }
}
