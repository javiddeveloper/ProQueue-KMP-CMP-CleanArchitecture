package xyz.sattar.javid.proqueue.feature.settings

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.about_app
import proqueue.composeapp.generated.resources.appName
import proqueue.composeapp.generated.resources.app_version
import proqueue.composeapp.generated.resources.cancel
import proqueue.composeapp.generated.resources.change_business
import proqueue.composeapp.generated.resources.compose_multiplatform
import proqueue.composeapp.generated.resources.delete
import proqueue.composeapp.generated.resources.delete_business
import proqueue.composeapp.generated.resources.delete_business_confirmation
import proqueue.composeapp.generated.resources.manage_notifications
import proqueue.composeapp.generated.resources.more_info
import proqueue.composeapp.generated.resources.notifications
import proqueue.composeapp.generated.resources.settings_menu_item
import proqueue.composeapp.generated.resources.smart_queue_management
import proqueue.composeapp.generated.resources.theme_appearance
import proqueue.composeapp.generated.resources.theme_settings
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
    onNavigateToAbout: () -> Unit = {},
    onChangeBusiness: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(SettingsIntent.LoadSettings)
    }

    HandleEvents(
        events = viewModel.events,
        onNavigateToAbout = onNavigateToAbout,
        onChangeBusiness = onChangeBusiness
    )

    SettingsScreenContent(
        uiState = uiState,
        onIntent = viewModel::sendIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.delete_business)) },
            text = { Text(stringResource(Res.string.delete_business_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onIntent(SettingsIntent.OnDeleteBusinessClick)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.settings_menu_item),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // App Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.compose_multiplatform),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.appName),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.smart_queue_management),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.app_version, uiState.appVersion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Change Business Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                SettingsItem(
                    icon = Icons.Default.Factory,
                    title = stringResource(Res.string.change_business),
                    subtitle = null,
                    onClick = { onIntent(SettingsIntent.OnChangeBusinessClick) },
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Delete Business Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = stringResource(Res.string.delete_business),
                    subtitle = null,
                    centerVertically = true,
                    onClick = { showDeleteDialog = true },
                    tint = MaterialTheme.colorScheme.error
                )
            }

            // Settings Options
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = stringResource(Res.string.theme_appearance),
                        subtitle = stringResource(Res.string.theme_settings),
                        onClick = { /* TODO */ }
                    )

                    HorizontalDivider()

                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = stringResource(Res.string.notifications),
                        subtitle = stringResource(Res.string.manage_notifications),
                        onClick = { /* TODO */ }
                    )

                    HorizontalDivider()

                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = stringResource(Res.string.about_app),
                        subtitle = stringResource(Res.string.more_info),
                        onClick = { onIntent(SettingsIntent.OnAboutClick) }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    centerVertically: Boolean = false,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = tint.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = tint
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tint.copy(alpha = 0.7f)
                )
            }
        }
        if (!centerVertically) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = tint.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun HandleEvents(
    events: Flow<SettingsEvent>,
    onNavigateToAbout: () -> Unit,
    onChangeBusiness: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            SettingsEvent.NavigateToAbout -> {
                scope.launch {
                    onNavigateToAbout()
                }
            }

            SettingsEvent.NavigateToBusinessSelection -> {
                scope.launch {
                    onChangeBusiness()
                }
            }

            SettingsEvent.BusinessDeleted -> {
                scope.launch {
                    onChangeBusiness()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    AppTheme {
        SettingsScreenContent(
            uiState = SettingsState(),
            onIntent = {}
        )
    }
}
