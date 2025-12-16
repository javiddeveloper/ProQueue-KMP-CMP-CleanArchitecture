package xyz.sattar.javid.proqueue.feature.createBusiness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.accept
import proqueue.composeapp.generated.resources.address
import proqueue.composeapp.generated.resources.business_name
import proqueue.composeapp.generated.resources.confirm
import proqueue.composeapp.generated.resources.create_business
import proqueue.composeapp.generated.resources.default_time_service
import proqueue.composeapp.generated.resources.phone
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.components.AppButton
import xyz.sattar.javid.proqueue.core.ui.components.AppTextField
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
    var titleError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var defaultProgressError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

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
            titleError = null
        },
        onPhone = {
            phone = it
            phoneError = null
        },
        onAddress = {
            address = it
            addressError = null
        },
        onDefaultProgress = {
            defaultProgress = it
            defaultProgressError = null
        },
        titleError = titleError,
        phoneError = phoneError,
        addressError = addressError,
        defaultProgressError = defaultProgressError,
        onTitleErrorUpdate = { titleError = it },
        onPhoneErrorUpdate = { phoneError = it },
        onAddressErrorUpdate = { addressError = it },
        onDefaultProgressErrorUpdate = { defaultProgressError = it },
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
    titleError: String? = null,
    phoneError: String? = null,
    addressError: String? = null,
    defaultProgressError: String? = null,
    onTitleErrorUpdate: (String?) -> Unit = {},
    onPhoneErrorUpdate: (String?) -> Unit = {},
    onAddressErrorUpdate: (String?) -> Unit = {},
    onDefaultProgressErrorUpdate: (String?) -> Unit = {},
) {
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.create_business),
                        style = MaterialTheme.typography.titleLarge
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
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

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
                isError = titleError != null,
                errorMessage = titleError,
                enabled = !uiState.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                enabled = !uiState.isLoading,
                maxLength = 3,
                value = defaultProgress,
                onValueChange = onDefaultProgress,
                label = stringResource(Res.string.default_time_service),
                isError = defaultProgressError != null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                errorMessage = defaultProgressError,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                enabled = !uiState.isLoading,
                maxLength = 11,
                value = phone,
                onValueChange = onPhone,
                label = stringResource(Res.string.phone),
                isError = phoneError != null,
                errorMessage = phoneError,
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
                isError = addressError != null,
                errorMessage = addressError,
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
                        val t = title.trim()
                        val p = phone.trim()
                        val a = address.trim()
                        val d = defaultProgress.trim()
                        val titleInvalid = t.length < 3
                        val phoneInvalid = p.length < 3
                        val defaultInvalid = d.isNotEmpty() && d.toIntOrNull() == null
                        onTitleErrorUpdate(if (titleInvalid) "نام کسب‌وکار صحیح نیست" else null)
                        onPhoneErrorUpdate(if (phoneInvalid) "شماره تلفن صحیح نیست" else null)
                        onAddressErrorUpdate(null)
                        onDefaultProgressErrorUpdate(if (defaultInvalid) "مدت زمان سرویس باید عدد باشد" else null)
                        if (!titleInvalid && !phoneInvalid && !defaultInvalid) {
                            onIntent(
                                CreateBusinessIntent.CreateBusiness(
                                    t,
                                    p,
                                    a,
                                    d
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading

                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            
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
            titleError = null,
            phoneError = null,
            addressError = null,
            defaultProgressError = null,
            onTitleErrorUpdate = {},
            onPhoneErrorUpdate = {},
            onAddressErrorUpdate = {},
            onDefaultProgressErrorUpdate = {},
        )
    }
}
