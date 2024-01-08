package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ReaderController(requestContext: RequestContext) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIBRARY_READER

    @GetMapping("/store/reader/epub")
    fun index(model: Model): String {
        return "store/reader"
    }

//    @GetMapping("/store/reader/epub")
//    fun epub(@RequestParam id: Long, response: HttpServletResponse) {
//        val product = productService.get(id)
//        if (product.fileContentType == "application/pdf" || product.fileUrl != null) {
//            val pdf = download(product.fileUrl!!)
//            val epub = Files.createTempFile(pdf.name, ".epub").toFile()
//            PdfConverter.convert(pdf).intoEpub(product.title, epub)
//            response.contentType = "application/epub+zip"
//            response.setContentLength(epub.length().toInt())
//            val fin = FileInputStream(epub)
//            fin.use {
//                IOUtils.copy(fin, response.outputStream)
//            }
//        } else {
//            throw NotFoundException(
//                error = Error()
//            )
//        }
//    }
//
//    @GetMapping("/store/reader/pdf")
//    fun pdf(@RequestParam id: Long, response: HttpServletResponse) {
//        val product = productService.get(id)
//        if (product.fileContentType == "application/pdf" || product.fileUrl != null) {
//            val pdf = download(product.fileUrl!!)
//            response.contentType = "application/pdf"
//            val fin = FileInputStream(pdf)
//            fin.use {
//                IOUtils.copy(fin, response.outputStream)
//            }
//        } else {
//            throw NotFoundException(
//                error = Error()
//            )
//        }
//    }
//
//    private fun download(url: String): File {
//        val file = File.createTempFile(UUID.randomUUID().toString(), ".pdf")
//        val fout = FileOutputStream(file)
//        fout.use {
//            IOUtils.copy(URL(url).openStream(), fout)
//        }
//        return file
//    }
}
