package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.Topic
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : CrudRepository<Topic, Long>
