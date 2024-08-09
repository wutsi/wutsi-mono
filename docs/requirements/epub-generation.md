# Context

Wutsi is converting e-books documents from PDF to the [EPUB](https://fr.wikipedia.org/wiki/EPUB_(format)) format so that
we can stream the document in the platform, instead of sending the original PDF file via email to customers.

**The problen:** The conversion from PDF to EPUB is completely manual, which make the process slow and non scalable, as
more authors join the platform

**The Ask:** We want to create tools for automating the conversion from PDF to EPUB

# The current process

1. Author create a product and upload the PDF, the he published the e-book
2. Wutsi clerk download the PDF, the convert the PDF to DOCX using https://www.convertio.co
3. Wutsi clerk edits the DOCX, and reformat the document using the following rules:
    1. Remove all empty paragraphs
    2. Add new line before each chapter
    3. Format the chapter title to H1
    4. etc.
4. Wutsi clerk convert the updated DOCX to EPUB using https://www.convertio.co
5. Wutsi upload the EPUB to the product

# The new process

For the new process, we want to create 2 tools for automating:

1. The conversion from PDF to DOCX and reformatting the document, using the python
   library [pdf2docx](https://pypi.org/project/pdf2docx/)
2. The conversion from DOCX to EPUB. QUESTION: is there available library for this?
