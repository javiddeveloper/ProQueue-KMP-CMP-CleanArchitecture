package xyz.sattar.javid.proqueue.core.utils

expect fun openSms(phone: String)
expect fun openWhatsApp(phone: String)
expect fun openTelegram(phone: String)
expect fun openPhoneDial(phone: String)
expect fun openUrl(url: String)
expect fun openInstagram(username: String)
expect fun openTwitter(username: String)

fun normalizePhone(input: String): String {
    val converted = buildString {
        for (ch in input) {
            append(
                when (ch) {
                    '۰' -> '0'
                    '۱' -> '1'
                    '۲' -> '2'
                    '۳' -> '3'
                    '۴' -> '4'
                    '۵' -> '5'
                    '۶' -> '6'
                    '۷' -> '7'
                    '۸' -> '8'
                    '۹' -> '9'
                    else -> ch
                }
            )
        }
    }
    val filtered = converted.filter { it.isDigit() || it == '+' }
    return if (filtered.startsWith("09")) {
        "+989" + filtered.drop(2)
    } else {
        filtered
    }
}
