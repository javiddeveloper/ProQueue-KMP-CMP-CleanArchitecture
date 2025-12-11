package xyz.sattar.javid.proqueue.feature.createVisitor

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.create_visitor
import proqueue.composeapp.generated.resources.phone
import proqueue.composeapp.generated.resources.register_visitor
import proqueue.composeapp.generated.resources.visitor_name
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.components.AppButton
import xyz.sattar.javid.proqueue.core.ui.components.AppTextField
import xyz.sattar.javid.proqueue.ui.theme.AppTheme
// removed duplicate imports
import proqueue.composeapp.generated.resources.edit
import proqueue.composeapp.generated.resources.edit_visitor
import androidx.compose.foundation.layout.WindowInsets

@Composable
fun CreateVisitorRoute(
    visitorId: Long? = null,
    viewModel: CreateVisitorViewModel = koinViewModel<CreateVisitorViewModel>(),
    onContinue: (Long) -> Unit,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    LaunchedEffect(visitorId) {
        if (visitorId != null) {
            viewModel.sendIntent(CreateVisitorIntent.LoadVisitor(visitorId))
        }
    }

    LaunchedEffect(uiState.loadedVisitor) {
        uiState.loadedVisitor?.let {
            fullName = it.fullName
            phoneNumber = it.phoneNumber
        }
    }

    HandleEvents(
        events = viewModel.events,
        onContinue = onContinue,
        onNavigateBack = onNavigateBack
    )

    CreateVisitorScreen(
        uiState = uiState,
        onIntent = { intent ->
            if (intent is CreateVisitorIntent.CreateVisitor) {
                viewModel.sendIntent(intent.copy(id = visitorId ?: 0))
            } else {
                viewModel.sendIntent(intent)
            }
        },
        fullName = fullName,
        phoneNumber = phoneNumber,
        onFullName = { fullName = it },
        onPhoneNumber = { phoneNumber = it },
        isEditing = visitorId != null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVisitorScreen(
    modifier: Modifier = Modifier,
    uiState: CreateVisitorState,
    onIntent: (CreateVisitorIntent) -> Unit,
    fullName: String,
    phoneNumber: String,
    onFullName: (String) -> Unit,
    onPhoneNumber: (String) -> Unit,
    isEditing: Boolean = false
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) stringResource(Res.string.edit_visitor) else stringResource(Res.string.create_visitor),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onIntent(CreateVisitorIntent.BackPress)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))


            AppTextField(
                value = fullName,
                onValueChange = onFullName,
                label = stringResource(Res.string.visitor_name),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                errorMessage = "",
                enabled = !uiState.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                enabled = !uiState.isLoading,
                value = phoneNumber,
                onValueChange = onPhoneNumber,
                label = stringResource(Res.string.phone),
                isError = false,
                errorMessage = "",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = if (isEditing) stringResource(Res.string.edit) else stringResource(Res.string.register_visitor),
                    onClick = {
                        onIntent(CreateVisitorIntent.CreateVisitor(fullName, phoneNumber))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading && fullName.isNotBlank()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            uiState.message?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun HandleEvents(
    events: Flow<CreateVisitorEvent>,
    onContinue: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            is CreateVisitorEvent.VisitorCreated -> {
                scope.launch {
                    onContinue(it.visitorId)
                }
            }

            CreateVisitorEvent.BackPressed -> {
                scope.launch {
                    onNavigateBack()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateVisitorScreen() {
    AppTheme {
        CreateVisitorScreen(
            uiState = CreateVisitorState(),
            onIntent = {},
            fullName = "",
            phoneNumber = "",
            onFullName = {},
            onPhoneNumber = {}
        )
    }
}
