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

actual fun openSms(phone: String) {
    openUrl("sms:${normalizePhone(phone)}")
}

actual fun openWhatsApp(phone: String) {
    openUrl("https://wa.me/${normalizePhone(phone)}")
}

actual fun openTelegram(phone: String) {
    openUrl("https://t.me/${normalizePhone(phone)}")
}

actual fun openPhoneDial(phone: String) {
    openUrl("tel:${normalizePhone(phone)}")
}

actual fun openInstagram(username: String) {
    openUrl("https://instagram.com/$username")
}

actual fun openTwitter(username: String) {
    openUrl("https://twitter.com/$username")
}
