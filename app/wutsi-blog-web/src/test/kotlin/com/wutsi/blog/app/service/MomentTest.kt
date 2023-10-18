package com.wutsi.blog.app.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.test.annotation.DirtiesContext
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Locale
import java.util.TimeZone

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class MomentTest {
    @MockBean
    private lateinit var clock: Clock

    @Autowired
    private lateinit var moment: Moment

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.ENGLISH)
    private val today = fmt.parse("2020-02-14 15:30:10")

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(Locale.ENGLISH)
        fmt.timeZone = TimeZone.getTimeZone("UTC")
    }

    @Test
    fun now() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Now", moment.format(DateUtils.addSeconds(today, 30)))
    }

    @Test
    fun minutes() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("30 minutes ago", moment.format(DateUtils.addMinutes(today, -30)))
        assertEquals("in 30 minutes", moment.format(DateUtils.addMinutes(today, 30)))
    }

    @Test
    fun hours() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("3 hours ago", moment.format(DateUtils.addHours(today, -3)))
        assertEquals("in 3 hours", moment.format(DateUtils.addHours(today, 3)))
    }

    @Test
    fun day() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Yesterday", moment.format(DateUtils.addDays(today, -1)))
        assertEquals("Tomorrow", moment.format(DateUtils.addDays(today, 1)))
    }

    @Test
    fun days() {
        // GIVEN
        doReturn(today.time).whenever(clock).millis()

        // THEN
        assertEquals("Feb. 11, 2020", moment.format(DateUtils.addDays(today, -3)))
        assertEquals("Feb. 17, 2020", moment.format(DateUtils.addDays(today, 3)))
    }
}
