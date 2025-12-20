package xyz.sattar.javid.proqueue.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.complete_action
import proqueue.composeapp.generated.resources.contact_options
import proqueue.composeapp.generated.resources.delete_appointment
import proqueue.composeapp.generated.resources.no_show_action
import proqueue.composeapp.generated.resources.overdue_time
import proqueue.composeapp.generated.resources.phone_call
import proqueue.composeapp.generated.resources.sms
import proqueue.composeapp.generated.resources.telegram
import proqueue.composeapp.generated.resources.to_label
import proqueue.composeapp.generated.resources.whatsapp
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.core.utils.buildReminderMessage
import xyz.sattar.javid.proqueue.core.utils.formatPhoneNumberForAction
import xyz.sattar.javid.proqueue.core.utils.openPhoneDial
import xyz.sattar.javid.proqueue.core.utils.openSms
import xyz.sattar.javid.proqueue.core.utils.openTelegram
import xyz.sattar.javid.proqueue.core.utils.openWhatsApp
import xyz.sattar.javid.proqueue.feature.home.QueueItem

@Composable
fun QueueItemCard(
    item: QueueItem,
    onRemove: () -> Unit,
    onComplete: () -> Unit,
    onNoShow: () -> Unit,
    onSendMessage: (appointmentId: Long, type: String, content: String, businessTitle: String) -> Unit,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val dateText = DateTimeUtils.formatDateTime(item.estimatedStartTime)
            val startTimeOnly = DateTimeUtils.formatTime(item.estimatedStartTime)
            val endTimeOnly = DateTimeUtils.formatTime(item.estimatedEndTime)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.visitorName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.visitorPhone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$endTimeOnly ${stringResource(Res.string.to_label)} $startTimeOnly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = (if (item.overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary).copy(
                        alpha = 0.12f
                    )
                ) {
                    Text(
                        text = item.waitingText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showMenu by remember { mutableStateOf(false) }
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(Res.string.contact_options)
                    )
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sms)) },
                        leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            val business = BusinessStateHolder.selectedBusiness.value
                            val businessTitle = business?.title ?: "--"
                            val businessAddress = business?.address ?: "--"
                            val message = buildReminderMessage(
                                businessId = item.appointment.businessId,
                                businessTitle = businessTitle,
                                businessAddress = businessAddress,
                                visitorName = item.visitorName,
                                appointmentMillis = item.appointment.appointmentDate,
                                reminderMinutes = item.waitingText,
                            )
                            openSms(formatPhoneNumberForAction(item.visitorPhone), message)
                            onSendMessage(item.appointment.id, "SMS", message, businessTitle)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.whatsapp)) },
                        leadingIcon = {
                            Icon(
                                painterResource(Res.drawable.whatsapp),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showMenu = false
                            val business = BusinessStateHolder.selectedBusiness.value
                            val businessTitle = business?.title ?: "--"
                            val businessAddress = business?.address ?: "--"
                            val message = buildReminderMessage(
                                businessId = item.appointment.businessId,
                                businessTitle = businessTitle,
                                businessAddress = businessAddress,
                                visitorName = item.visitorName,
                                appointmentMillis = item.appointment.appointmentDate,
                                reminderMinutes = item.waitingText,
                                )
                            openWhatsApp(formatPhoneNumberForAction(item.visitorPhone), message)
                            onSendMessage(item.appointment.id, "WHATSAPP", message, businessTitle)
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.telegram)) },
                        leadingIcon = { Icon(Icons.Default.Send, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            val business = BusinessStateHolder.selectedBusiness.value
                            val businessTitle = business?.title ?: "--"
                            val businessAddress = business?.address ?: "--"
                            val message = buildReminderMessage(
                                businessId = item.appointment.businessId,
                                businessTitle = businessTitle,
                                businessAddress = businessAddress,
                                visitorName = item.visitorName,
                                appointmentMillis = item.appointment.appointmentDate,
                                reminderMinutes = item.waitingText,
                                )
                            openTelegram(formatPhoneNumberForAction(item.visitorPhone), message)
                            onSendMessage(item.appointment.id, "TELEGRAM", message, businessTitle)
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.phone_call)) },
                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            openPhoneDial(item.visitorPhone)
                        })
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            modifier = Modifier.clickable { onComplete() },
                            contentDescription = stringResource(Res.string.complete_action),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(Res.string.complete_action),
                            modifier = Modifier.clickable { onComplete() },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.clickable { onNoShow() },
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.no_show_action),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = stringResource(Res.string.no_show_action),
                            modifier = Modifier.clickable { onNoShow() },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.clickable { onRemove() },
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete_appointment),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(Res.string.delete_appointment),
                            modifier = Modifier.clickable { onRemove() },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
