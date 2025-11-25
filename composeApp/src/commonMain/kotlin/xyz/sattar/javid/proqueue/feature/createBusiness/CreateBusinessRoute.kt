package xyz.sattar.javid.proqueue.feature.createBusiness

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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

    HandleEvents(
        events = viewModel.events,
        navigateToVisitors = navigateToVisitors,
    )

    CreateBusinessScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent
    )
}

@Composable
fun CreateBusinessScreen(
    modifier: Modifier = Modifier,
    uiState: CreateBusinessState,
    onIntent: (CreateBusinessIntent) -> Unit
) {
    TODO("Not yet implemented")
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
        onIntent = {},
        uiState = CreateBusinessState(),
    )

}