[![](https://github.com/wutsi/wutsi-mono/actions/workflows/libs-wutsi-editorjs-master.yml/badge.svg)](https://github.com/wutsi/wutsi-mono/actions/workflows/libs-wutsi-editorjs-master.yml)

![](https://img.shields.io/badge/jdk-1.8-brightgreen.svg)
![](https://img.shields.io/badge/language-kotlin-blue.svg)

Library for manipulating [Editorjs](https://editorjs.io/) documents.

# Features

- [EJSDocument](https://github.com/wutsi-mono/libs/wutsi-editorjs/blob/master/src/main/kotlin/com/wutsi/editorjs/dom/EJSDocument.kt)
  support EditorJS DOM structure
- [EJSJsonReader](https://github.com/wutsi-mono/libs/wutsi-editorjs/blob/master/src/main/kotlin/com/wutsi/editorjs/json/EJSJsonReader.kt)
  converts a JSON string to a `EJSDocument`
- [EJSJsonWriter](https://github.com/wutsi-mono/libs/wutsi-editorjs/blob/master/src/main/kotlin/com/wutsi/editorjs/json/EJSJsonWriter.kt)
  converts a `EJSDocument` to a JSON string.
- [EJSHtmlReader](https://github.com/wutsi-mono/libs/wutsi-editorjs/blob/master/src/main/kotlin/com/wutsi/editorjs/html/EJSHtmlReader.kt)
  converts a HTML string to a `EJSDocument`
- [EJSHtmlWriter](https://github.com/wutsi-mono/libs/wutsi-editorjs/blob/master/src/main/kotlin/com/wutsi/editorjs/html/EJSHtmlWriter.kt)
  converts a `EJSDocument` to a HTML string.
