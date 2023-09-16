package com.wutsi.ml.similarity.model.story

import com.wutsi.ml.similarity.model.AbstractSimilarityModelTest
import org.springframework.beans.factory.annotation.Autowired

class StoryTfidfSimilarityModelTest : AbstractSimilarityModelTest() {
    @Autowired
    private lateinit var model: StoryTfidfSimilarityModel

    override fun getModel() = model
}
