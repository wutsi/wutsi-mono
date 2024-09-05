package com.wutsi.blog.product.service.metadata

import com.wutsi.blog.product.service.DocumentPreviewGenerator
import io.documentnode.epub4j.domain.Book
import io.documentnode.epub4j.domain.Resource
import io.documentnode.epub4j.epub.EpubReader
import io.documentnode.epub4j.epub.EpubWriter
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.max
import kotlin.math.min

@Service
class EPUBPreviewGenerator : DocumentPreviewGenerator {
    companion object {
        const val MAX_SIZE = 5
        const val MIN_SIZE = 3
    }

    override fun generate(`in`: InputStream, out: OutputStream): Boolean {
        // Load the book
        val book0 = EpubReader().readEpub(`in`)
        val size0 = book0.spine.spineReferences.size

        // Preview
        val size = max(min(MAX_SIZE, size0 / 10), MIN_SIZE)
        var i = 0
        if (size < size0) {
            val book = Book()
            book0.spine
                .spineReferences
                .take(size)
                .forEach { spine ->
                    i++
                    book.addSection(
                        spine.resource.title ?: "Section $i",
                        Resource(spine.resource.reader, spine.resource.href)
                    )
                }
            EpubWriter().write(book, out)
            return true
        }
        return false
    }
}
