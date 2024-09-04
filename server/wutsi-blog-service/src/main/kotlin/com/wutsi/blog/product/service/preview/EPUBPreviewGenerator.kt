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

@Service
class EPUBPreviewExtractor : DocumentPreviewGenerator {
    companion object {
        const val SIZE = 10
    }

    override fun generate(`in`: InputStream, out: OutputStream): Boolean {
        // Load the book
        val book0 = EpubReader().readEpub(`in`)
        val size0 = book0.tableOfContents.size()

        // Preview
        val size = max(SIZE, size0 / 10)
        if (size < size0) {
            val book = Book()
            book0.tableOfContents
                .tocReferences
                .take(size)
                .forEach { toc ->
                    book.addSection(toc.title, Resource(toc.resource.reader, toc.resource.href))
                }
            EpubWriter().write(book, out)
            return true
        }
        return false
    }
}
