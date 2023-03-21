package com.wutsi.membership.access.service

import com.wutsi.enums.PlaceType
import com.wutsi.membership.access.dao.PlaceRepository
import com.wutsi.membership.access.dto.Place
import com.wutsi.membership.access.dto.PlaceSummary
import com.wutsi.membership.access.dto.SavePlaceRequest
import com.wutsi.membership.access.dto.SearchPlaceRequest
import com.wutsi.membership.access.entity.PlaceEntity
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.access.util.StringUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.text.Normalizer
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipFile
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class PlaceService(
    private val dao: PlaceRepository,
    private val em: EntityManager,
    private val logger: KVLogger,
) {
    companion object {
        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        private val MIN_POPULATION = 1000
        private const val RECORD_ID = 0
        private const val RECORD_NAME = 1
        private const val RECORD_LATITUDE = 4
        private const val RECORD_LONGITUDE = 5
        private const val RECORD_FEATURE_CLASS = 6
        private const val RECORD_FEATURE_CODE = 7
        private const val RECORD_COUNTRY = 8
        private const val RECORD_POPULATION = 14
        private const val RECORD_TIMEZONE = 17
    }

    fun findById(id: Long): PlaceEntity =
        dao.findById(id).orElseThrow {
            NotFoundException(
                error = Error(
                    code = ErrorURN.PLACE_NOT_FOUND.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }

    fun save(request: SavePlaceRequest): PlaceEntity {
        val place = dao.findById(request.id)
            .orElse(PlaceEntity())
        place.id = request.id
        place.name = request.name
        place.nameAscii = StringUtil.toAscii(request.name)
        place.longitude = request.longitude
        place.latitude = request.latitude
        place.timezoneId = request.timezoneId
        place.type = PlaceType.valueOf(request.type.uppercase())
        place.country = request.country.uppercase()
        return dao.save(place)
    }

    fun getLongName(place: PlaceEntity, language: String?): String {
        val locale = Locale(language ?: "en", place.country)
        val displayCountry = locale.getDisplayCountry(locale)
        return "${place.name}, $displayCountry"
    }

    fun search(request: SearchPlaceRequest): List<PlaceEntity> {
        val query = em.createQuery(sql(request))
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<PlaceEntity>
    }

    fun toPlace(place: PlaceEntity, language: String?) = Place(
        id = place.id,
        name = place.name,
        longName = getLongName(place, language),
        latitude = place.latitude,
        longitude = place.longitude,
        country = place.country,
        type = place.type.name,
        timezoneId = place.timezoneId,
    )

    fun toPlaceSummary(place: PlaceEntity, language: String?) = PlaceSummary(
        id = place.id,
        name = place.name,
        longName = getLongName(place, language),
        country = place.country,
        type = place.type.name,
    )

    fun import(country: String) {
        var row = 0
        var imported = 0
        var errors = 0
        val url = URL("https://download.geonames.org/export/dump/${country.uppercase()}.zip")
        val file = download(country.uppercase(), url)
        try {
            val parser = CSVParser.parse(
                file,
                Charsets.UTF_8,
                CSVFormat.Builder.create()
                    .setDelimiter("\t")
                    .build(),
            )
            for (record in parser) {
                if (accept(record, country)) {
                    val logger = DefaultKVLogger()
                    log(row, record, logger)
                    try {
                        doSave(record)
                        imported++
                    } catch (ex: Exception) {
                        errors++
                        logger.setException(ex)
                    } finally {
                        logger.log()
                        row++
                    }
                }
            }
        } finally {
            logger.add("csv_rows", row)
            logger.add("csv_imported", imported)
            logger.add("csv_errors", errors)
            logger.add("file", file)
            logger.add("url", url)
        }
    }

    private fun download(country: String, url: URL): File {
        val seed = UUID.randomUUID().toString()
        val zip = File(System.getProperty("java.io.tmpdir"), "$seed-$country.zip")
        zip.deleteOnExit()
        try {
            url.openStream().use {
                Files.copy(it, zip.toPath())
            }

            // Extract
            ZipFile(zip).use { z ->
                val entry = z.entries().asSequence().find { it.name == "$country.txt" }
                if (entry != null) {
                    val file = File(System.getProperty("java.io.tmpdir"), "$seed-$country.txt")
                    file.deleteOnExit()

                    z.getInputStream(entry).use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                        return file
                    }
                }
            }

            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PLACE_FEED_NOT_FOUND.urn,
                    parameter = Parameter(
                        name = "country",
                        value = country,
                    ),
                    data = mapOf("url" to url.toString()),
                ),
            )
        } catch (ex: Exception) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PLACE_FEED_NOT_FOUND.urn,
                    parameter = Parameter(
                        name = "country",
                        value = country,
                    ),
                    data = mapOf("url" to url.toString()),
                ),
                ex,
            )
        }
    }

    private fun accept(record: CSVRecord, country: String): Boolean {
        return country == record.get(RECORD_COUNTRY) &&
            record.get(RECORD_FEATURE_CLASS) == "P" &&
            listOf(
                "PPL",
                "PPLC",
                "PPLA",
                "PPLA2",
                "PPLA3",
                "PPLA4",
                "PPLA5",
            ).contains(record.get(RECORD_FEATURE_CODE)) &&
            record.get(RECORD_POPULATION) != null &&
            record.get(RECORD_POPULATION).toInt() >= MIN_POPULATION
    }

    private fun doSave(record: CSVRecord) {
        save(
            request = SavePlaceRequest(
                id = record.get(RECORD_ID).toLong(),
                name = record.get(RECORD_NAME),
                country = record.get(RECORD_COUNTRY),
                type = PlaceType.CITY.name,
                longitude = record.get(RECORD_LONGITUDE).toDouble(),
                latitude = record.get(RECORD_LATITUDE).toDouble(),
                timezoneId = record.get(RECORD_TIMEZONE),
            ),
        )
    }

    private fun log(row: Int, record: CSVRecord, logger: KVLogger) {
        logger.add("row", row)
        logger.add("record_id", record.get(RECORD_ID))
        logger.add("record_name", record.get(RECORD_NAME))
        logger.add("record_feature_class", record.get(RECORD_FEATURE_CLASS))
        logger.add("record_feature_code", record.get(RECORD_FEATURE_CODE))
        logger.add("record_population", record.get(RECORD_POPULATION))
        logger.add("record_latitude", record.get(RECORD_LATITUDE))
        logger.add("record_longitude", record.get(RECORD_LONGITUDE))
        logger.add("record_timezone", record.get(RECORD_TIMEZONE))
    }

    private fun sql(request: SearchPlaceRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where ORDER BY a.nameAscii"
        }
    }

    private fun select(): String =
        "SELECT a FROM PlaceEntity a"

    private fun where(request: SearchPlaceRequest): String {
        val criteria = mutableListOf<String>()

        if (!request.country.isNullOrEmpty()) {
            criteria.add("a.country=:country")
        }
        if (request.type != null) {
            criteria.add("a.type = :type")
        }
        if (request.keyword != null) {
            criteria.add("UCASE(a.nameAscii) LIKE :keyword")
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchPlaceRequest, query: Query) {
        if (!request.country.isNullOrEmpty()) {
            query.setParameter("country", request.country.uppercase())
        }
        if (request.type != null) {
            query.setParameter("type", PlaceType.valueOf(request.type.uppercase()))
        }
        if (request.keyword != null) {
            query.setParameter("keyword", unaccent(request.keyword).uppercase() + "%")
        }
    }

    fun unaccent(str: String?): String {
        if (str == null) {
            return ""
        }

        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
