package xyz.sattar.javid.proqueue.core.notifications

import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptionBadge
import platform.UserNotifications.UNNotification
import platform.darwin.NSObject
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IosNotificationScheduler : NotificationScheduler {
    
    init {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.delegate = object : NSObject(), UNUserNotificationCenterDelegateProtocol {
            override fun userNotificationCenter(
                center: UNUserNotificationCenter,
                willPresentNotification: UNNotification,
                withCompletionHandler: (platform.UserNotifications.UNNotificationPresentationOptions) -> Unit
            ) {
                withCompletionHandler(
                    UNNotificationPresentationOptionAlert or 
                    UNNotificationPresentationOptionSound or 
                    UNNotificationPresentationOptionBadge
                )
            }
        }
    }

    override fun scheduleReminder(
        appointmentId: Long,
        customerName: String,
        businessName: String,
        triggerAtMillis: Long,
        minutesBefore: Int
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        
        val content = UNMutableNotificationContent().apply {
            setTitle("یادآوری نوبت")
            setBody("نوبت $customerName در کسب و کار $businessName تا $minutesBefore دقیقه دیگر نزدیک هست برای اطلاع از نوبت اینجا را لمس کنید")
            setSound(UNNotificationSound.defaultSound())
        }

        val timeInterval = (triggerAtMillis - DateTimeUtils.systemCurrentMilliseconds()) / 1000.0
        // Ensure strictly positive time interval
        val safeTimeInterval = if (timeInterval <= 0) 1.0 else timeInterval

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = safeTimeInterval,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = appointmentId.toString(),
            content = content,
            trigger = trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error scheduling notification: ${error.localizedDescription}")
            }
        }
    }

    override fun cancelReminder(appointmentId: Long) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(appointmentId.toString()))
    }

    override suspend fun hasPermission(): Boolean = suspendCoroutine { cont ->
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            cont.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }
}
