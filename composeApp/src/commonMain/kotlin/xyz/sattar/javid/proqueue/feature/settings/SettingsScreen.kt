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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import proqueue.composeapp.generated.resources.app_version
import proqueue.composeapp.generated.resources.compose_multiplatform
import proqueue.composeapp.generated.resources.logout
import proqueue.composeapp.generated.resources.manage_notifications
import proqueue.composeapp.generated.resources.more_info
import proqueue.composeapp.generated.resources.notifications
import proqueue.composeapp.generated.resources.settings_menu_item
import proqueue.composeapp.generated.resources.theme_appearance
import proqueue.composeapp.generated.resources.theme_settings
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(SettingsIntent.LoadSettings)
    }

    HandleEvents(
        events = viewModel.events,
        onNavigateToAbout = onNavigateToAbout,
        onLogout = onLogout
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
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    text = "ProQueue",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.app_version, uiState.appVersion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                ) {
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

            // Logout Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = stringResource(Res.string.logout),
                    subtitle = null,
                    onClick = { onIntent(SettingsIntent.OnLogoutClick) },
                    tint = MaterialTheme.colorScheme.error
                )
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
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = tint
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
        
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = tint.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun HandleEvents(
    events: Flow<SettingsEvent>,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            SettingsEvent.NavigateToAbout -> {
                scope.launch {
                    onNavigateToAbout()
                }
            }
            SettingsEvent.Logout -> {
                scope.launch {
                    onLogout()
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
