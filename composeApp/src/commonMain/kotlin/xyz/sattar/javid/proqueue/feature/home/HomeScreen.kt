package xyz.sattar.javid.proqueue.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.home_menu_item
import proqueue.composeapp.generated.resources.queue_title
import proqueue.composeapp.generated.resources.to_label
import proqueue.composeapp.generated.resources.welcome_to_proqueue
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(HomeIntent.LoadData)
    }

    HandleEvents(
        events = viewModel.events
    )

    HomeScreenContent(
        uiState = uiState,
        onIntent = viewModel::sendIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.home_menu_item),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Welcome Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth().wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        uiState.business?.title?.ifEmpty { stringResource(Res.string.welcome_to_proqueue) }
                            ?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.business?.address ?: "--",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                // Dashboard Stats
                DashboardStatsSection(stats = uiState.stats)
            }

            item {
                Text(
                    text = stringResource(Res.string.queue_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(uiState.queue) { queueItem ->
                QueueItemCard(
                    item = queueItem,
                    onRemove = { onIntent(HomeIntent.RemoveAppointment(queueItem.appointment.id)) },
                    onComplete = { onIntent(HomeIntent.MarkAppointmentCompleted(queueItem.appointment.id)) },
                    onNoShow = { onIntent(HomeIntent.MarkAppointmentNoShow(queueItem.appointment.id)) },
                    onSendMessage = { type ->
                        onIntent(
                            HomeIntent.SendMessage(
                                queueItem.appointment.id,
                                type
                            )
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun DashboardStatsSection(stats: DashboardStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Total", // TODO: Res
            value = stats.totalVisitors.toString(),
            color = MaterialTheme.colorScheme.secondaryContainer
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Cancelled", // TODO: Res
            value = stats.cancelledVisitors.toString(),
            color = MaterialTheme.colorScheme.errorContainer
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Avg/Day", // TODO: Res
            value = stats.avgVisitorsPerDay.toString(),
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Peak", // TODO: Res
            value = stats.peakHours,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge)
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

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
                    text = "زمان رد شده",
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
                    // TODO: Add Icons for SMS, WhatsApp, Telegram
                    // For now using Text placeholders
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

@Composable
fun HandleEvents(
    events: Flow<HomeEvent>
) {
    events.collectWithLifecycleAware {}
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    AppTheme {
        HomeScreenContent(
            uiState = HomeState(),
            onIntent = {}
        )
    }
}
