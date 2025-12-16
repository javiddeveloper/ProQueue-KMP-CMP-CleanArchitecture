package xyz.sattar.javid.proqueue.core.utils

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

private fun launchUri(uri: String) {
    val context = xyz.sattar.javid.proqueue.ProQueueApp.appContext
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

actual fun openSms(phone: String) {
    launchUri("sms:$phone")
}

actual fun openWhatsApp(phone: String) {
    launchUri("https://wa.me/$phone")
}

actual fun openTelegram(phone: String) {
    launchUri("https://t.me/$phone")
}

actual fun openPhoneDial(phone: String) {
    val context = xyz.sattar.javid.proqueue.ProQueueApp.appContext
    val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

actual fun openUrl(url: String) {
    launchUri(url)
}

actual fun openInstagram(username: String) {
    launchUri("https://instagram.com/$username")
}

actual fun openTwitter(username: String) {
    launchUri("https://twitter.com/$username")
}
