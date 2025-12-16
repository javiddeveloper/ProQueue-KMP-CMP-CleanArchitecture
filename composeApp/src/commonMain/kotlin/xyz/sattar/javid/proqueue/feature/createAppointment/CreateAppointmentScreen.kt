package xyz.sattar.javid.proqueue.feature.createAppointment

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.appointment_create_action
import proqueue.composeapp.generated.resources.appointment_time
import proqueue.composeapp.generated.resources.back
import proqueue.composeapp.generated.resources.cancel
import proqueue.composeapp.generated.resources.choose_time
import proqueue.composeapp.generated.resources.confirm
import proqueue.composeapp.generated.resources.create_appointment_title
import proqueue.composeapp.generated.resources.edit_appointment
import proqueue.composeapp.generated.resources.hour_label
import proqueue.composeapp.generated.resources.minute_label
import proqueue.composeapp.generated.resources.select_visitor
import proqueue.composeapp.generated.resources.service_duration_minutes
import proqueue.composeapp.generated.resources.service_duration_error
import proqueue.composeapp.generated.resources.conflict_dialog_title
import proqueue.composeapp.generated.resources.conflict_dialog_message_prefix
import proqueue.composeapp.generated.resources.conflict_dialog_message_suffix
import proqueue.composeapp.generated.resources.yes_force_create
import proqueue.composeapp.generated.resources.no
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.components.AppButton
import xyz.sattar.javid.proqueue.core.ui.components.AppTextField
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.ui.theme.AppTheme
import kotlin.time.ExperimentalTime

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Color

@Composable
fun CreateAppointmentScreen(
    visitorId: Long? = null,
    appointmentId: Long? = null,
    viewModel: CreateAppointmentViewModel = koinViewModel<CreateAppointmentViewModel>(),
    onNavigateBack: () -> Unit = {},
    onAppointmentCreated: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (appointmentId != null) {
            viewModel.sendIntent(CreateAppointmentIntent.LoadAppointment(appointmentId))
        } else if (visitorId != null) {
            viewModel.sendIntent(CreateAppointmentIntent.SelectVisitor(visitorId))
        }
    }
//    LaunchedEffect(uiState.appointmentCreated) {
//        if (uiState.appointmentCreated) {
//            onAppointmentCreated()
//        }
//    }

    HandleEvents(
        events = viewModel.events,
        onNavigateBack = onNavigateBack,
        onAppointmentCreated = onAppointmentCreated
    )

    CreateAppointmentScreenContent(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        initialVisitorId = visitorId
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CreateAppointmentScreenContent(
    modifier: Modifier = Modifier,
    uiState: CreateAppointmentState,
    onIntent: (CreateAppointmentIntent) -> Unit,
    initialVisitorId: Long? = null
) {
    var selectedVisitorId by remember { mutableStateOf(initialVisitorId) }
    var selectedDate by remember { mutableStateOf(DateTimeUtils.systemCurrentMilliseconds()) }
    var selectedTime by remember { mutableStateOf("09:00") }
    var serviceDuration by remember { mutableStateOf(uiState.serviceDuration?.toString() ?: "30") }
    var serviceDurationError by remember { mutableStateOf<String?>(null) }
    var showTimeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState.selectedVisitorId != null) {
            selectedVisitorId = uiState.selectedVisitorId
        }
        if (uiState.appointmentDate != 0L) {
            selectedDate = uiState.appointmentDate
            // Extract time from timestamp
            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(uiState.appointmentDate)
            val localDateTime =
                instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')
            selectedTime = "$hour:$minute"
        }
        if (uiState.serviceDuration != null) {
            serviceDuration = uiState.serviceDuration.toString()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.message) {
        val msg = uiState.message
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text(stringResource(Res.string.confirm))
                        }
                    }
                ) {
                    Text(data.visuals.message)
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.create_appointment_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(CreateAppointmentIntent.BackPress) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (uiState.appointmentCreated) {
                onIntent(CreateAppointmentIntent.AppointmentCreated)
            } else if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Visitor Selection
                    Text(
                        text = stringResource(Res.string.select_visitor),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (selectedVisitorId != null) {
                                        uiState.visitor?.fullName
                                            ?: "--"
                                    } else {
                                        "--"
                                    },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                selectedVisitorId?.let { id ->
                                    uiState.visitor?.phoneNumber?.let { phone ->
                                        Text(
                                            text = phone,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Time Selection
                    Text(
                        text = stringResource(Res.string.appointment_time),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimeDialog = true },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedTime,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    }

                    // Service Duration
                    Text(
                        text = stringResource(Res.string.service_duration_minutes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    AppTextField(
                        value = serviceDuration,
                        maxLength = 3,
                        keyboardType = KeyboardType.Number,
                        onValueChange = {
                            serviceDuration = it
                            serviceDurationError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        isError = serviceDurationError != null,
                        errorMessage = serviceDurationError,
                        enabled = !uiState.isLoading,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    

                    // Create/Update Button
                    val serviceDurationErrorMsg = stringResource(Res.string.service_duration_error)
                    AppButton(
                        text = if (uiState.editingAppointmentId != null) stringResource(Res.string.edit_appointment) else stringResource(Res.string.appointment_create_action),
                        onClick = {
                            selectedVisitorId?.let { visitorId ->
                                // Parse time and create timestamp
                                val duration = serviceDuration.trim().toIntOrNull()
                                serviceDurationError = if (duration == null) serviceDurationErrorMsg else null
                                onIntent(
                                    CreateAppointmentIntent.CreateAppointment(
                                        visitorId = visitorId,
                                        appointmentDate = DateTimeUtils.combineDateAndTime(
                                            selectedDate,
                                            selectedTime
                                        ),
                                        serviceDuration = duration
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedVisitorId != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Time Selection Dialog
            if (showTimeDialog) {
                TimeSelectionDialog(
                    selectedTime = selectedTime,
                    onTimeSelected = { time ->
                        selectedTime = time
                        showTimeDialog = false
                    },
                    onDismiss = { showTimeDialog = false }
                )
            }

            // Conflict Dialog
            if (uiState.showConflictDialog) {
                val prefix = stringResource(Res.string.conflict_dialog_message_prefix)
                val suffix = stringResource(Res.string.conflict_dialog_message_suffix)
                AlertDialog(
                    onDismissRequest = { onIntent(CreateAppointmentIntent.DismissConflictDialog) },
                    title = { Text(stringResource(Res.string.conflict_dialog_title)) },
                    text = {
                        Text(
                            text = buildAnnotatedString {
                                append(prefix)
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                ) {
                                    append(uiState.conflictingVisitorName ?: "")
                                }
                                append(suffix)
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedVisitorId?.let { visitorId ->
                                    val duration = serviceDuration.trim().toIntOrNull()
                                    onIntent(
                                        CreateAppointmentIntent.CreateAppointment(
                                            visitorId = visitorId,
                                            appointmentDate = DateTimeUtils.combineDateAndTime(
                                                selectedDate,
                                                selectedTime
                                            ),
                                            serviceDuration = duration,
                                            force = true
                                        )
                                    )
                                }
                            }
                        ) {
                            Text(stringResource(Res.string.yes_force_create))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onIntent(CreateAppointmentIntent.DismissConflictDialog) }) {
                            Text(stringResource(Res.string.no))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TimeSelectionDialog(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(selectedTime.split(":")[0].toIntOrNull() ?: 9) }
    var minute by remember { mutableStateOf(selectedTime.split(":")[1].toIntOrNull() ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.choose_time)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hour Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(Res.string.hour_label), style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = { if (hour > 0) hour-- }) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                    }
                    Text(
                        text = hour.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { if (hour < 23) hour++ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }

                // Minute Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(Res.string.minute_label), style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = { if (minute >= 15) minute -= 15 }) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                    }
                    Text(
                        text = minute.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { if (minute < 45) minute += 15 }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val timeString =
                        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                    onTimeSelected(timeString)
                }
            ) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}

@Composable
fun HandleEvents(
    events: Flow<CreateAppointmentEvent>,
    onNavigateBack: () -> Unit,
    onAppointmentCreated: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            CreateAppointmentEvent.NavigateBack -> {
                onNavigateBack()

            }

            CreateAppointmentEvent.AppointmentCreated -> {
                onAppointmentCreated()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAppointmentScreenPreview() {
    AppTheme {
        CreateAppointmentScreenContent(
            uiState = CreateAppointmentState(),
            onIntent = {}
        )
    }
}
