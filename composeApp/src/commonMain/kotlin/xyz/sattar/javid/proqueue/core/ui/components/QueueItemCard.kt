package xyz.sattar.javid.proqueue.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.to_label
import proqueue.composeapp.generated.resources.overdue_time
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.feature.home.QueueItem

@Composable
fun QueueItemCard(
    item: QueueItem,
    onRemove: () -> Unit,
    onComplete: () -> Unit,
    onNoShow: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = item.visitorName, style = MaterialTheme.typography.titleMedium)
                    Text(text = item.visitorPhone, style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = DateTimeUtils.formatDateTime(item.estimatedStartTime),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${stringResource(Res.string.to_label)} ${
                            DateTimeUtils.formatTime(
                                item.estimatedEndTime
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val overdue =
                DateTimeUtils.systemCurrentMilliseconds() > item.estimatedEndTime && item.appointment.status == "WAITING"
            if (overdue) {
                Text(
                    text = stringResource(Res.string.overdue_time),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Message Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("SMS", modifier = Modifier.clickable { onSendMessage("SMS") })
                    Text("WA", modifier = Modifier.clickable { onSendMessage("WHATSAPP") })
                    Text("TG", modifier = Modifier.clickable { onSendMessage("TELEGRAM") })
                }

                // Queue Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Done",
                        modifier = Modifier.clickable { onComplete() },
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "NoShow",
                        modifier = Modifier.clickable { onNoShow() },
                        color = MaterialTheme.colorScheme.error
                    )
                    Text("Del", modifier = Modifier.clickable { onRemove() })
                }
            }
        }
    }
}

