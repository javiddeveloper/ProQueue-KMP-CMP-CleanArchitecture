package xyz.sattar.javid.proqueue.feature.createBusiness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware

@Composable
fun CreateBusinessRoute(
    viewModel: CreateBusinessViewModel = koinViewModel<CreateBusinessViewModel>(),
    navigateToVisitors: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }


    LaunchedEffect(Unit){
        viewModel.sendIntent(CreateBusinessIntent.LoadBusiness)
    }

    HandleEvents(
        events = viewModel.events,
        navigateToVisitors = navigateToVisitors,

        )

    CreateBusinessScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        title = title,
        phone = phone,
        address = address,
        onTitle = {
            title = it
        },
        onPhone = {
            phone = it
        },
        onAddress = {
            address = it
        },
    )
}

@Composable
fun CreateBusinessScreen(
    modifier: Modifier = Modifier,
    uiState: CreateBusinessState,
    onIntent: (CreateBusinessIntent) -> Unit,
    title: String,
    phone: String,
    address: String,
    onTitle: (String) -> Unit,
    onPhone: (String) -> Unit,
    onAddress: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "ایجاد کسب‌وکار",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitle,
            label = { Text("نام کسب‌وکار") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhone,
            label = { Text("تلفن") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = address,
            onValueChange = onAddress,
            label = { Text("آدرس") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            minLines = 3
        )

        Button(
            onClick = {
                onIntent(CreateBusinessIntent.CreateBusiness(title, phone, address))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && title.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("ایجاد")
            }
        }

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

@Composable
fun HandleEvents(
    events: Flow<CreateBusinessEvent>,
    navigateToVisitors: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            CreateBusinessEvent.NavigateToVisitors -> {
                scope.launch {
                    navigateToVisitors()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    CreateBusinessScreen(
        uiState = CreateBusinessState(),
        onIntent = {},
        title = "",
        phone = "",
        address = "",
        onTitle = {},
        onPhone = {},
        onAddress = {}
    )
}