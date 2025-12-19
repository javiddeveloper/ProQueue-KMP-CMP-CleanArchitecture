package xyz.sattar.javid.proqueue.feature.messages

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.messages_title
import proqueue.composeapp.generated.resources.messages_tokens_label
import proqueue.composeapp.generated.resources.messages_preview_label
import proqueue.composeapp.generated.resources.messages_save
import xyz.sattar.javid.proqueue.core.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(MessagesIntent.Load)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(Res.string.messages_title)) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.messages_tokens_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                MessageToken.values().forEach { token ->
                    TokenChip(label = token.label) {
                        viewModel.sendIntent(MessagesIntent.InsertToken(token.token))
                    }
                }
            }

            OutlinedTextField(
                value = uiState.template,
                onValueChange = { viewModel.sendIntent(MessagesIntent.UpdateTemplate(it)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8
            )

            Text(
                text = stringResource(Res.string.messages_preview_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(text = uiState.preview, style = MaterialTheme.typography.bodyLarge)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
            ) {
                uiState.readyTemplates.forEachIndexed { index, tpl ->
                    SuggestionChip(
                        onClick = { viewModel.sendIntent(MessagesIntent.ApplyReadyTemplate(tpl)) },
                        label = { Text("الگو ${index + 1}") }
                    )
                }
            }

            AppButton(
                text = stringResource(Res.string.messages_save),
                onClick = { viewModel.sendIntent(MessagesIntent.Save) })

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TokenChip(label: String, onClick: () -> Unit) {
    SuggestionChip(onClick = onClick, label = { Text(label) })
}

enum class MessageToken(val label: String, val token: String) {
    Visitor("نام مشتری", "{visitor}"),
    Business("نام کسب‌وکار", "{business}"),
    Address("آدرس", "{address}"),
    Date("تاریخ", "{date}"),
    Time("ساعت", "{time}"),
    Minutes("دقیقه یادآوری", "{minutes}")
}
