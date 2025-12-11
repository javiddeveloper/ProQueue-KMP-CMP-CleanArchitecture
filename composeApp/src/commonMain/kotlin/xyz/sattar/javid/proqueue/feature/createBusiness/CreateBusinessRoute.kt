package xyz.sattar.javid.proqueue.feature.createBusiness

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
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
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
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.accept
import proqueue.composeapp.generated.resources.address
import proqueue.composeapp.generated.resources.business_name
import proqueue.composeapp.generated.resources.create_business
import proqueue.composeapp.generated.resources.default_time_service
import proqueue.composeapp.generated.resources.phone
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.components.AppButton
import xyz.sattar.javid.proqueue.core.ui.components.AppTextField
import xyz.sattar.javid.proqueue.core.ui.components.ProfileImagePicker
import xyz.sattar.javid.proqueue.ui.theme.AppTheme

@Composable
fun CreateBusinessRoute(
    viewModel: CreateBusinessViewModel = koinViewModel<CreateBusinessViewModel>(),
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var defaultProgress by remember { mutableStateOf("") }

    HandleEvents(
        events = viewModel.events,
        onContinue = onContinue,
        onNavigateBack = onNavigateBack
    )
//    LaunchedEffect(uiState.businessCreated){
//        if(uiState.businessCreated){
//            onContinue()
//        }
//    }
    CreateBusinessScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        title = title,
        phone = phone,
        address = address,
        defaultProgress = defaultProgress,
        onTitle = {
            title = it
        },
        onPhone = {
            phone = it
        },
        onAddress = {
            address = it
        },
        onDefaultProgress = {
            defaultProgress = it
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBusinessScreen(
    modifier: Modifier = Modifier,
    uiState: CreateBusinessState,
    onIntent: (CreateBusinessIntent) -> Unit,
    title: String,
    phone: String,
    address: String,
    defaultProgress: String,
    onTitle: (String) -> Unit,
    onPhone: (String) -> Unit,
    onAddress: (String) -> Unit,
    onDefaultProgress: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.create_business),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onIntent(CreateBusinessIntent.BackPress)
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
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.businessCreated) {
            onIntent(CreateBusinessIntent.BusinessCreated)
        }
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

            ProfileImagePicker(imageUrl = null)

            AppTextField(
                value = title,
                onValueChange = onTitle,
                label = stringResource(Res.string.business_name),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Factory,
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
                value = defaultProgress,
                onValueChange = onDefaultProgress,
                label = stringResource(Res.string.default_time_service),
                isError = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                errorMessage = "",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                enabled = !uiState.isLoading,
                value = phone,
                onValueChange = onPhone,
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

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                enabled = !uiState.isLoading,
                value = address,
                onValueChange = onAddress,
                label = stringResource(Res.string.address),
                isError = false,
                errorMessage = "",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                maxLine = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = stringResource(Res.string.accept),
                    onClick = {
                        onIntent(
                            CreateBusinessIntent.CreateBusiness(
                                title,
                                phone,
                                address,
                                defaultProgress
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading && title.isNotBlank()

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
            if (uiState.business != null) {
                Text(uiState.business.title)
            }
        }
    }
}

@Composable
fun HandleEvents(
    events: Flow<CreateBusinessEvent>,
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            CreateBusinessEvent.NavigateToBusiness -> {
                onContinue()
            }

            CreateBusinessEvent.BackPressed -> {
                onNavigateBack()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    AppTheme {
        CreateBusinessScreen(
            uiState = CreateBusinessState(),
            onIntent = {},
            title = "",
            phone = "",
            address = "",
            defaultProgress = "",
            onTitle = {},
            onPhone = {},
            onAddress = {},
            onDefaultProgress = {},
        )
    }
}
