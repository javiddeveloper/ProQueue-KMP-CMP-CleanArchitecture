package xyz.sattar.javid.proqueue.feature.messages

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TokenChip(label = "{visitor}") { viewModel.sendIntent(MessagesIntent.InsertToken("{visitor}")) }
                TokenChip(label = "{business}") { viewModel.sendIntent(MessagesIntent.InsertToken("{business}")) }
                TokenChip(label = "{date}") { viewModel.sendIntent(MessagesIntent.InsertToken("{date}")) }
                TokenChip(label = "{time}") { viewModel.sendIntent(MessagesIntent.InsertToken("{time}")) }
                TokenChip(label = "{minutes}") { viewModel.sendIntent(MessagesIntent.InsertToken("{minutes}")) }
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = { viewModel.sendIntent(MessagesIntent.ApplyReadyTemplate(defaultTemplates()[0])) }, label = { Text("Template 1") })
                SuggestionChip(onClick = { viewModel.sendIntent(MessagesIntent.ApplyReadyTemplate(defaultTemplates()[1])) }, label = { Text("Template 2") })
                SuggestionChip(onClick = { viewModel.sendIntent(MessagesIntent.ApplyReadyTemplate(defaultTemplates()[2])) }, label = { Text("Template 3") })
            }

            Button(onClick = { viewModel.sendIntent(MessagesIntent.Save) }) {
                Text(text = stringResource(Res.string.messages_save))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TokenChip(label: String, onClick: () -> Unit) {
    SuggestionChip(onClick = onClick, label = { Text(label) })
}

private fun defaultTemplates(): List<String> = listOf(
    "با سلام {visitor} عزیز؛ نوبت شما در {business} ساعت {time} می‌باشد. لطفاً حدود {minutes} دقیقه دیگر حضور داشته باشید.",
    "{visitor} عزیز؛ یادآوری نوبت: {date} ساعت {time} در {business}. لطفاً {minutes} دقیقه زودتر تشریف بیاورید.",
    "دوست عزیز {visitor}؛ نوبت شما در {business}، {date} - {time}. حضور شما تا {minutes} دقیقه دیگر لازم است."
)

