package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dto.ImportError
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL

@Service
class ProductImporterValidator {
    fun validate(row: Int, record: CSVRecord): List<ImportError> {
        val errors = mutableListOf<String>()
        if (record.get("id")?.trim().isNullOrEmpty()) {
            errors.add(ErrorCode.PRODUCT_ID_MISSING)
        }

        if (record.get("title")?.trim().isNullOrEmpty()) {
            errors.add(ErrorCode.PRODUCT_TITLE_MISSING)
        }

        if (record.get("file_link")?.trim().isNullOrEmpty()) {
            errors.add(ErrorCode.PRODUCT_FILE_LINK_MISSING)
        } else {
            validateLink(record.get("file_link"), ErrorCode.PRODUCT_FILE_LINK_INVALID, errors)
        }

        if (record.get("image_link")?.trim().isNullOrEmpty()) {
            errors.add(ErrorCode.PRODUCT_IMAGE_LINK_MISSING)
        } else {
            validateLink(record.get("image_link"), ErrorCode.PRODUCT_IMAGE_LINK_INVALID, errors)
        }

        val price = record.get("price")?.trim()
        if (price.isNullOrEmpty()) {
            errors.add(ErrorCode.PRODUCT_PRICE_MISSING)
        } else {
            try {
                val value = price.toLong()
                if (value == 0L) {
                    errors.add(ErrorCode.PRODUCT_PRICE_ZERO)
                }
            } catch (ex: Exception) {
                errors.add(ErrorCode.PRODUCT_PRICE_INVALID)
            }
        }
        return errors.map {
            ImportError(row, it, id = record.get("id"))
        }
    }

    private fun validateLink(url: String, errorCode: String, errors: MutableList<String>) {
        try {
            URL(url)
        } catch (ex: MalformedURLException) {
            errors.add(errorCode)
        }
    }
}
