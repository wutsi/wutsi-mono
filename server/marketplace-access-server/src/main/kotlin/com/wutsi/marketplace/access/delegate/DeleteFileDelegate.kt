package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.service.FileService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class DeleteFileDelegate(private val service: FileService) {
    @Transactional
    public fun invoke(id: Long) {
        service.delete(id)
    }
}
