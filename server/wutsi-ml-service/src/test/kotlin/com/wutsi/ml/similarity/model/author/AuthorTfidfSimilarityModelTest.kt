package com.wutsi.ml.similarity.model.author

import com.wutsi.ml.similarity.model.AbstractSimilarityModelTest
import org.springframework.beans.factory.annotation.Autowired

class AuthorTfidfSimilarityModelTest : AbstractSimilarityModelTest() {
    @Autowired
    private lateinit var model: AuthorTfidfSimilarityModel

    override fun getModel() = model
}
