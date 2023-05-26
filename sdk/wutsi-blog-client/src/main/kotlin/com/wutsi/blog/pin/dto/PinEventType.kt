package com.wutsi.blog.pin.dto

object PinEventType {
    const val PIN_STORY_COMMAND = "urn:wutsi:command:pin-story"
    const val UNPIN_STORY_COMMAND = "urn:wutsi:command:unpin-story"

    const val STORY_PINED_EVENT = "urn:wutsi:event:story-pined"
    const val STORY_UNPINED_EVENT = "urn:wutsi:event:story-unpined"
}
