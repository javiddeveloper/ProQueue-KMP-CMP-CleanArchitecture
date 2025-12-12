package xyz.sattar.javid.proqueue.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

private fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}

actual fun openSms(phone: String) {
    openUrl("sms:$phone")
}

actual fun openWhatsApp(phone: String) {
    openUrl("https://wa.me/$phone")
}

actual fun openTelegram(phone: String) {
    openUrl("https://t.me/$phone")
}

actual fun openPhoneDial(phone: String) {
    openUrl("tel:$phone")
}

