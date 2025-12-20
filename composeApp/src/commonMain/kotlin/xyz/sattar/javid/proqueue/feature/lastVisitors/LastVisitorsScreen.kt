package xyz.sattar.javid.proqueue.feature.lastVisitors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.create_appointment
import proqueue.composeapp.generated.resources.empty_appointments_subtitle
import proqueue.composeapp.generated.resources.empty_appointments_title
import proqueue.composeapp.generated.resources.last_visitors_menu_item
import proqueue.composeapp.generated.resources.overdue_time
import proqueue.composeapp.generated.resources.people_in_queue_count
import proqueue.composeapp.generated.resources.queue_tab
import proqueue.composeapp.generated.resources.status_cancelled
import proqueue.composeapp.generated.resources.status_completed
import proqueue.composeapp.generated.resources.status_no_show
import proqueue.composeapp.generated.resources.status_waiting
import proqueue.composeapp.generated.resources.to_label
import proqueue.composeapp.generated.resources.total_visitors_count
import proqueue.composeapp.generated.resources.visitors_tab
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.components.EmptyState
import xyz.sattar.javid.proqueue.core.ui.components.QueueItemCard
import xyz.sattar.javid.proqueue.core.ui.components.SectionTabs
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.domain.model.Appointment
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.model.Visitor
import xyz.sattar.javid.proqueue.feature.home.QueueItem
import xyz.sattar.javid.proqueue.ui.theme.AppTheme
import kotlin.math.abs

@Composable
fun LastVisitorsScreen(
    viewModel: LastVisitorsViewModel = koinViewModel<LastVisitorsViewModel>(),
    onNavigateToCreateAppointment: () -> Unit = {},
    onNavigateToEditAppointment: (Long) -> Unit = {},
    onNavigateToVisitorDetails: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(LastVisitorsIntent.LoadAppointments)
    }

    HandleEvents(
        events = viewModel.events,
        onNavigateToCreateAppointment = onNavigateToCreateAppointment,
        onNavigateToEditAppointment = onNavigateToEditAppointment,
        onNavigateToVisitorDetails = onNavigateToVisitorDetails
    )

    LastVisitorsScreenContent(
        uiState = uiState,
        onIntent = viewModel::sendIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastVisitorsScreenContent(
    modifier: Modifier = Modifier,
    uiState: LastVisitorsState,
    onIntent: (LastVisitorsIntent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.last_visitors_menu_item),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onIntent(LastVisitorsIntent.OnCreateAppointmentClick)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_appointment)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.appointments.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center),
                        icon = Icons.Default.EventNote,
                        title = stringResource(Res.string.empty_appointments_title),
                        subtitle = stringResource(Res.string.empty_appointments_subtitle)
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SectionTabs(
                            labels = listOf(
                                stringResource(Res.string.visitors_tab),
                                stringResource(Res.string.queue_tab),
                            ),
                            selectedIndex = uiState.selectedTab,
                            onSelected = { index -> onIntent(LastVisitorsIntent.OnTabSelected(index)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.selectedTab == 1) {
                            val now = DateTimeUtils.systemCurrentMilliseconds()
                            val waiting = uiState.appointments
                                .filter { it.appointment.status == "WAITING" }
                                .sortedBy { abs(it.appointment.appointmentDate - now) }

                            TotalCountHeader(
                                title = stringResource(Res.string.people_in_queue_count),
                                count = waiting.size
                            )

                            if (waiting.isEmpty()) {
                                EmptyState(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    icon = Icons.Default.EventNote,
                                    title = stringResource(Res.string.empty_appointments_title),
                                    subtitle = stringResource(Res.string.empty_appointments_subtitle)
                                )
                            } else {
                                val queueItems = waiting.map { item ->
                                    val duration = (item.appointment.serviceDuration
                                        ?: item.business.defaultServiceDuration) * 60 * 1000L
                                    QueueItem(
                                        appointment = item.appointment,
                                        visitorName = item.visitor.fullName,
                                        visitorPhone = item.visitor.phoneNumber,
                                        estimatedStartTime = item.appointment.appointmentDate,
                                        estimatedEndTime = item.appointment.appointmentDate + duration
                                    )
                                }
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(queueItems) { queueItem ->
                                        QueueItemCard(
                                            item = queueItem,
                                            onRemove = {
                                                onIntent(
                                                    LastVisitorsIntent.OnDeleteAppointment(
                                                        queueItem.appointment.id
                                                    )
                                                )
                                            },
                                            onComplete = {
                                                onIntent(
                                                    LastVisitorsIntent.OnMarkCompleted(
                                                        queueItem.appointment.id
                                                    )
                                                )
                                            },
                                            onNoShow = {
                                                onIntent(LastVisitorsIntent.OnMarkNoShow(queueItem.appointment.id))
                                            },
                                            onSendMessage = { appointmentId, type, content, businessTitle ->
                                                onIntent(
                                                    LastVisitorsIntent.OnSendMessage(
                                                        appointmentId = appointmentId,
                                                        type = type,
                                                        content = content,
                                                        businessTitle = businessTitle
                                                    )
                                                )
                                            },
                                            onItemClick = {
                                                onIntent(LastVisitorsIntent.OnAppointmentClick(queueItem.appointment.visitorId))
                                            }
                                        )
                                    }
                                    item { Spacer(modifier = Modifier.height(80.dp)) }
                                }
                            }
                        } else {
                            TotalCountHeader(
                                title = stringResource(Res.string.total_visitors_count),
                                count = uiState.totalCount
                            )
                            AppointmentsList(
                                appointments = uiState.appointments,
                                onEditClick = { appointmentId ->
                                    onIntent(LastVisitorsIntent.OnEditAppointment(appointmentId))
                                },
                                onDeleteClick = { appointmentId ->
                                    onIntent(LastVisitorsIntent.OnDeleteAppointment(appointmentId))
                                },
                                onItemClick = { visitorId ->
                                    onIntent(LastVisitorsIntent.OnAppointmentClick(visitorId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalCountHeader(
    title: String,
    count: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun AppointmentsList(
    appointments: List<AppointmentWithDetails>,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onItemClick: (Long) -> Unit
) {
    if (appointments.isEmpty()) {
        EmptyState(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            icon = Icons.Default.EventNote,
            title = stringResource(Res.string.empty_appointments_title),
            subtitle = stringResource(Res.string.empty_appointments_subtitle)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCard(
                    appointmentWithDetails = appointment,
                    onEditClick = { onEditClick(appointment.appointment.id) },
                    onDeleteClick = { onDeleteClick(appointment.appointment.id) },
                    onItemClick = { onItemClick(appointment.appointment.visitorId) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun AppointmentCard(
    appointmentWithDetails: AppointmentWithDetails,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
    ) {
        val appointment = appointmentWithDetails.appointment
        val visitor = appointmentWithDetails.visitor
        var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visitor Info (compact)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val dateText = DateTimeUtils.formatDateTime(appointment.appointmentDate)
                val endTimeMs = appointment.appointmentDate + (appointment.serviceDuration
                    ?: appointmentWithDetails.business.defaultServiceDuration) * 60 * 1000L
                val timeRange =
                    " ${DateTimeUtils.formatTime(endTimeMs)} ${stringResource(Res.string.to_label)} ${
                        DateTimeUtils.formatTime(
                            appointment.appointmentDate
                        )
                    }"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = visitor.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
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
                        text = visitor.phoneNumber ?: "--",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = timeRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val durationMinutes = appointment.serviceDuration
                    ?: appointmentWithDetails.business.defaultServiceDuration
                val endTime = appointment.appointmentDate + durationMinutes * 60 * 1000L
                val overdue =
                    DateTimeUtils.systemCurrentMilliseconds() > endTime && appointment.status == "WAITING"
                val waitingOrOverdueText =
                    if (overdue) stringResource(Res.string.overdue_time) else DateTimeUtils.calculateWaitingTime(
                        appointment.appointmentDate
                    )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusBadge(status = appointment.status, overdue = overdue)
                }

                val description = appointment.description
                if (!description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Options menu removed per request
        }
    }
}

@Composable
fun StatusBadge(status: String, overdue: Boolean) {
    val (text, color) = when {
        status == "WAITING" && overdue -> stringResource(Res.string.overdue_time) to MaterialTheme.colorScheme.error
        status == "WAITING" -> stringResource(Res.string.status_waiting) to MaterialTheme.colorScheme.primary
        status == "COMPLETED" -> stringResource(Res.string.status_completed) to MaterialTheme.colorScheme.tertiary
        status == "NO_SHOW" -> stringResource(Res.string.status_no_show) to MaterialTheme.colorScheme.error
        status == "CANCELLED" -> stringResource(Res.string.status_cancelled) to MaterialTheme.colorScheme.error
        else -> status to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}




@Composable
fun HandleEvents(
    events: Flow<LastVisitorsEvent>,
    onNavigateToCreateAppointment: () -> Unit,
    onNavigateToEditAppointment: (Long) -> Unit,
    onNavigateToVisitorDetails: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            LastVisitorsEvent.NavigateToCreateAppointment -> {
                scope.launch {
                    onNavigateToCreateAppointment()
                }
            }

            is LastVisitorsEvent.NavigateToEditAppointment -> {
                scope.launch {
                    onNavigateToEditAppointment(it.appointmentId)
                }
            }

            is LastVisitorsEvent.NavigateToVisitorDetails -> {
                scope.launch {
                    onNavigateToVisitorDetails(it.visitorId)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLastVisitorsScreen() {
    AppTheme {
        LastVisitorsScreenContent(
            uiState = LastVisitorsState(
                appointments = listOf(
                    AppointmentWithDetails(
                        appointment = Appointment(
                            id = 1,
                            businessId = 1,
                            visitorId = 1,
                            appointmentDate = DateTimeUtils.systemCurrentMilliseconds(),
                            serviceDuration = 30,
                            status = "WAITING",
                            createdAt = 0,
                            updatedAt = 0,
                            description = ""
                        ),
                        visitor = Visitor(
                            id = 1,
                            fullName = "علی احمدی",
                            phoneNumber = "09121234567",
                            createdAt = 0
                        ),
                        business = Business(
                            id = 1,
                            title = "آرایشگاه",
                            phone = "",
                            address = "",
                            logoPath = "",
                            defaultServiceDuration = 30,
                            workStartHour = 9,
                            workEndHour = 17,
                            notificationEnabled = false,
                            notificationTypes = "",
                            notificationMinutesBefore = 30,
                            createdAt = 0
                        )
                    )
                ),
                totalCount = 1
            ),
            onIntent = {}
        )
    }
}
