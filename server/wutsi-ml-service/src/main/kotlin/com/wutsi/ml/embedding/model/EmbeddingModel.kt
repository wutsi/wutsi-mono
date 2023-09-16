package com.wutsi.ml.embedding.model

interface EmbeddingModel {
    fun getNNIndexPath(): String

    fun getEmbeddingPath(): String

    fun build(): Long
}
