package xyz.sattar.javid.proqueue.feature.lastVisitors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.formatTime
import xyz.sattar.javid.proqueue.core.ui.systemCurrentMilliseconds
import xyz.sattar.javid.proqueue.domain.model.Appointment
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.model.Visitor
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
fun LastVisitorsScreen(
    viewModel: LastVisitorsViewModel = koinViewModel<LastVisitorsViewModel>(),
    onNavigateToCreateAppointment: () -> Unit = {},
    onNavigateToEditAppointment: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(LastVisitorsIntent.LoadAppointments)
    }

    HandleEvents(
        events = viewModel.events,
        onNavigateToCreateAppointment = onNavigateToCreateAppointment,
        onNavigateToEditAppointment = onNavigateToEditAppointment
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "نوبت‌های امروز",
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
                    contentDescription = "ایجاد نوبت جدید"
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
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header with count
                        TotalCountHeader(count = uiState.totalCount)

                        // Appointments list
                        AppointmentsList(
                            appointments = uiState.appointments,
                            onOptionsClick = { appointmentId ->
                                onIntent(LastVisitorsIntent.OnAppointmentOptionsClick(appointmentId))
                            }
                        )
                    }
                }
            }

            // Options Dialog
            if (uiState.showOptionsDialog && uiState.selectedAppointmentId != null) {
                AppointmentOptionsDialog(
                    onEdit = {
                        onIntent(LastVisitorsIntent.OnEditAppointment(uiState.selectedAppointmentId))
                    },
                    onDelete = {
                        onIntent(LastVisitorsIntent.OnDeleteAppointment(uiState.selectedAppointmentId))
                    },
                    onDismiss = {
                        onIntent(LastVisitorsIntent.DismissDialog)
                    }
                )
            }
        }
    }
}

@Composable
fun TotalCountHeader(count: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تعداد کل نوبت‌های امروز",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AppointmentsList(
    appointments: List<AppointmentWithDetails>,
    onOptionsClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        items(appointments) { appointmentWithDetails ->
            AppointmentCard(
                appointmentWithDetails = appointmentWithDetails,
                onOptionsClick = { onOptionsClick(appointmentWithDetails.appointment.id) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) } // Space for FAB
    }
}

@Composable
fun AppointmentCard(
    appointmentWithDetails: AppointmentWithDetails,
    onOptionsClick: () -> Unit
) {
    val appointment = appointmentWithDetails.appointment
    val visitor = appointmentWithDetails.visitor

    Card(
        modifier = Modifier.fillMaxWidth(),
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
            // Queue Position Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appointment.queuePosition.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Visitor Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = visitor.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = visitor.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTime(appointment.appointmentDate) ?: "--:--",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    appointment.serviceDuration?.let { duration ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• $duration دقیقه",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = appointment.status)
            }

            // Options Button
            IconButton(onClick = onOptionsClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "گزینه‌ها",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (text, color) = when (status) {
        "WAITING" -> "در انتظار" to MaterialTheme.colorScheme.primary
        "COMPLETED" -> "تکمیل شده" to MaterialTheme.colorScheme.tertiary
        "NO_SHOW" -> "عدم حضور" to MaterialTheme.colorScheme.error
        "CANCELLED" -> "لغو شده" to MaterialTheme.colorScheme.error
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
fun AppointmentOptionsDialog(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("مدیریت نوبت")
        },
        text = {
            Column {
                TextButton(
                    onClick = {
                        onEdit()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تغییر نوبت")
                }

                TextButton(
                    onClick = {
                        onDelete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف نوبت")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("بستن")
            }
        }
    )
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EventNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "هنوز نوبتی ثبت نشده",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "با زدن دکمه + می‌توانید نوبت جدید ایجاد کنید",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HandleEvents(
    events: Flow<LastVisitorsEvent>,
    onNavigateToCreateAppointment: () -> Unit,
    onNavigateToEditAppointment: (Long) -> Unit
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
                            appointmentDate = systemCurrentMilliseconds(),
                            serviceDuration = 30,
                            status = "WAITING",
                            queuePosition = 1,
                            createdAt = 0,
                            updatedAt = 0
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
