package xyz.sattar.javid.proqueue.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openUrl(url: String) {
    val encodedUrl = url.replace(" ", "%20")
    val nsUrl = NSURL.URLWithString(encodedUrl)
    if (nsUrl != null) {
        if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any>(), null)
        }
    }
}

private fun sanitizePhone(phone: String): String {
    return phone.filter { it.isDigit() || it == '+' }
}

actual fun openSms(phone: String) {
    openUrl("sms:${sanitizePhone(phone)}")
}

actual fun openWhatsApp(phone: String) {
    openUrl("https://wa.me/${sanitizePhone(phone)}")
}

actual fun openTelegram(phone: String) {
    openUrl("https://t.me/${sanitizePhone(phone)}")
}

actual fun openPhoneDial(phone: String) {
    openUrl("tel:${sanitizePhone(phone)}")
}

actual fun openInstagram(username: String) {
    openUrl("https://instagram.com/$username")
}

actual fun openTwitter(username: String) {
    openUrl("https://twitter.com/$username")
}

