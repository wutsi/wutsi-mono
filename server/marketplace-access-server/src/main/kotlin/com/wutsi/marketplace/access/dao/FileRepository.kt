package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.FileEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<FileEntity, Long>
