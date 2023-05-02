package com.wutsi.blog.channel.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.Fixtures
import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.story.service.StoryService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ChannelListenerTest {
    @Mock
    lateinit var stories: StoryService

    @Mock
    lateinit var registry: ChannelRegistry

    @InjectMocks
    lateinit var listener: ChannelListener

    @Test
    fun `publish Story to channels when flag is enabled`() {
        val story = Fixtures.createStory(11, publishToSocialMedia = true)
        doReturn(story).whenever(stories).findById(11)

        listener.onPublish(PublishEvent(11))

        verify(registry).publishStory(story)
    }
}
