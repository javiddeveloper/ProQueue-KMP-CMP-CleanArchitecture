package xyz.sattar.javid.proqueue.feature.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.enable_notifications
import proqueue.composeapp.generated.resources.notification_reminder_hint
import proqueue.composeapp.generated.resources.notification_reminder_time
import proqueue.composeapp.generated.resources.notifications
import proqueue.composeapp.generated.resources.save_settings
import proqueue.composeapp.generated.resources.settings_saved
import xyz.sattar.javid.proqueue.core.permissions.rememberNotificationPermissionLauncher
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberNotificationPermissionLauncher { granted ->
        viewModel.sendIntent(NotificationsIntent.PermissionResult(granted))
    }

    HandleEffects(
        events = viewModel.events,
        onNavigateBack = onNavigateBack,
        showSnackbar = { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        },
        onRequestPermission = {
            permissionLauncher.launch()
        }
    )

    NotificationsScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onIntent = viewModel::sendIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenContent(
    uiState: NotificationsState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onIntent: (NotificationsIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.notifications)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Enable Notifications Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.enable_notifications),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = uiState.isNotificationsEnabled,
                            onCheckedChange = { onIntent(NotificationsIntent.ToggleNotifications(it)) }
                        )
                    }

                    // Reminder Time Input
                    if (uiState.isNotificationsEnabled) {
                        OutlinedTextField(
                            value = uiState.reminderMinutes,
                            onValueChange = { onIntent(NotificationsIntent.UpdateReminderMinutes(it)) },
                            label = { Text(stringResource(Res.string.notification_reminder_time)) },
                            placeholder = { Text(stringResource(Res.string.notification_reminder_hint)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = { onIntent(NotificationsIntent.SaveSettings) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(Res.string.save_settings),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun HandleEffects(
    events: Flow<NotificationsEvent>,
    onNavigateBack: () -> Unit,
    showSnackbar: (String) -> Unit,
    onRequestPermission: () -> Unit
) {
    val savedMessage = stringResource(Res.string.settings_saved)
    events.collectWithLifecycleAware { event ->
        when (event) {
            NotificationsEvent.NavigateBack -> onNavigateBack()
            NotificationsEvent.ShowSavedConfirmation -> showSnackbar(savedMessage)
            NotificationsEvent.RequestPermission -> onRequestPermission()
        }
    }
}
