package xyz.sattar.javid.proqueue.core.utils

import xyz.sattar.javid.proqueue.core.prefs.PreferencesManager

fun buildReminderMessage(
    businessId: Long,
    businessTitle: String,
    businessAddress: String = "",
    visitorName: String,
    appointmentMillis: Long,
    reminderMinutes: String = PreferencesManager.getNotificationReminderMinutes().toString()
): String {
    val template = PreferencesManager.getMessageTemplate(businessId)
        ?: "با سلام {visitor} عزیز؛ نوبت شما در {business} ساعت {time} می‌باشد. لطفاً حدود {minutes} دقیقه دیگر حضور داشته باشید."
    val date = DateTimeUtils.formatDate(appointmentMillis)
    val time = DateTimeUtils.formatTime(appointmentMillis)
    return template
        .replace("{visitor}", visitorName)
        .replace("{business}", businessTitle)
        .replace("{address}", businessAddress)
        .replace("{date}", date)
        .replace("{time}", time)
        .replace("{minutes}", reminderMinutes)
}
