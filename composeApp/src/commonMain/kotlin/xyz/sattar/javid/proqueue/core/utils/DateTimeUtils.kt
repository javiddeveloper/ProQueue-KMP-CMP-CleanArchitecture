package xyz.sattar.javid.proqueue.core.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object DateTimeUtils {

    @OptIn(ExperimentalTime::class)
    fun endOfTodayOfMonthMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        val now = Clock.System.now()
        val todayOfMonth = now.toLocalDateTime(timeZone).date
        val tomorrowStart = todayOfMonth.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
        return tomorrowStart.toEpochMilliseconds() - 1
    }

    @OptIn(ExperimentalTime::class)
    fun systemCurrentMilliseconds(): Long =
        Clock.System.now().toEpochMilliseconds()


    @OptIn(ExperimentalTime::class)
    fun formatMillisDateOnly(millis: Long): String {
        val dateTime =
            Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())

        val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = dateTime.month.name.lowercase()
            .replaceFirstChar { it.titlecase() } // "January"
            .take(3) // → "Jan"
        val year = dateTime.year

        return "$dayOfMonth-$month-$year"
    }

    @OptIn(ExperimentalTime::class)
    fun formatTimeNow(): String {
        val currentMillis: Long = Clock.System.now().toEpochMilliseconds()
        val dateTime = Instant.fromEpochMilliseconds(currentMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
        val minute = dateTime.minute.toString().padStart(2, '0')
        val amPm = if (dateTime.hour < 12) "AM" else "PM"
        return "$hour:$minute $amPm" // 00:00 AM
    }

    @OptIn(ExperimentalTime::class)
    fun formatMillisWithTimeNow(): String {
        val currentMillis: Long = Clock.System.now().toEpochMilliseconds()
        val dateTime = Instant.fromEpochMilliseconds(currentMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val hour = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
        val minute = dateTime.minute.toString().padStart(2, '0')
        val amPm = if (dateTime.hour < 12) "AM" else "PM"

        val dayOfMonth = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = dateTime.month.name.lowercase()
            .replaceFirstChar { it.titlecase() } // "January"
            .take(3) // → "Jan"
        val year = dateTime.year

        return "$hour:$minute $amPm $dayOfMonth-$month-$year" // 00:00 AM 00-Jan-0000
    }

    fun calculateWaitingTime(appointmentDate: Long): String {
        val now = systemCurrentMilliseconds()
        val diff = appointmentDate - now
        if (diff <= 0) return "زمان نوبت فرا رسیده"

        val duration = diff.milliseconds
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60

        if (hours == 0L && minutes == 0L) {
            return "کمتر از یک دقیقه تا نوبت"
        }

        val hoursPart = if (hours > 0) "$hours ساعت" else ""
        val minutesPart = if (minutes > 0) "$minutes دقیقه" else ""
        val separator = if (hours > 0 && minutes > 0) " و " else ""
        
        return "$hoursPart$separator$minutesPart زمان انتظار"
    }

    fun calculateWaitingOrOverdueText(
        appointmentDate: Long,
        serviceDurationMinutes: Int,
        status: String
    ): String {
        val endTime = appointmentDate + serviceDurationMinutes * 60 * 1000L
        val overdue = systemCurrentMilliseconds() > endTime && status == "WAITING"
        return if (overdue) "زمان رد شده" else calculateWaitingTime(appointmentDate)
    }

    fun formatDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val persianDate = gregorianToJalali(
            localDateTime.year,
            localDateTime.monthNumber,
            localDateTime.dayOfMonth
        )

        return "${persianDate.year}/${
            persianDate.month.toString().padStart(2, '0')
        }/${persianDate.dayOfMonth.toString().padStart(2, '0')}"
    }

    fun formatDateTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val persianDate = gregorianToJalali(
            localDateTime.year,
            localDateTime.monthNumber,
            localDateTime.dayOfMonth
        )

        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')

        return "$hour:$minute  ${persianDate.year}/${
            persianDate.month.toString().padStart(2, '0')
        }/${persianDate.dayOfMonth.toString().padStart(2, '0')}"
    }

    fun formatTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    data class PersianDate(val year: Int, val month: Int, val dayOfMonth: Int)

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): PersianDate {
        val g_d_m = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        var gy2 = if (gm > 2) gy + 1 else gy
        var dayOfMonths =
            355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + g_d_m[gm - 1]
        var jy = -1595 + (33 * (dayOfMonths / 12053))
        dayOfMonths %= 12053
        jy += 4 * (dayOfMonths / 1461)
        dayOfMonths %= 1461
        if (dayOfMonths > 365) {
            jy += (dayOfMonths - 1) / 365
            dayOfMonths = (dayOfMonths - 1) % 365
        }
        val jm = if (dayOfMonths < 186) 1 + (dayOfMonths / 31) else 7 + ((dayOfMonths - 186) / 30)
        val jd = 1 + if (dayOfMonths < 186) (dayOfMonths % 31) else ((dayOfMonths - 186) % 30)
        return PersianDate(jy, jm, jd)
    }

    fun combineDateAndTime(dateMillis: Long, timeString: String): Long {
        val timeParts = timeString.split(":")
        val hour = timeParts[0].toIntOrNull() ?: 0
        val minute = timeParts[1].toIntOrNull() ?: 0

        val instant = Instant.fromEpochMilliseconds(dateMillis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // We need to construct a new LocalDateTime with the same date but new time
        // Since kotlinx-datetime doesn't have a simple 'withTime' method that returns a timestamp easily without more ceremony,
        // and we want to avoid complex timezone math if possible, we can try to adjust the timestamp.
        // However, the safest way with kotlinx-datetime is to reconstruct the LocalDateTime.

        val newLocalDateTime = kotlinx.datetime.LocalDateTime(
            year = localDate.year,
            monthNumber = localDate.month.number,
            dayOfMonth = localDate.dayOfMonth,
            hour = hour,
            minute = minute,
            second = 0,
            nanosecond = 0
        )
        return newLocalDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }
}
