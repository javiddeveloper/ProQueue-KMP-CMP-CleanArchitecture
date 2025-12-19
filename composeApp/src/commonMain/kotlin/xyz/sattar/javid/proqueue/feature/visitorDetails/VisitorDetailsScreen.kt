package xyz.sattar.javid.proqueue.feature.visitorDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import proqueue.composeapp.generated.resources.Res
import proqueue.composeapp.generated.resources.appointments_tab
import proqueue.composeapp.generated.resources.business_name
import proqueue.composeapp.generated.resources.contact_options
import proqueue.composeapp.generated.resources.empty_messages_subtitle
import proqueue.composeapp.generated.resources.empty_messages_title
import proqueue.composeapp.generated.resources.messages_tab
import proqueue.composeapp.generated.resources.visitor_details_title
import proqueue.composeapp.generated.resources.whatsapp
import xyz.sattar.javid.proqueue.core.ui.collectWithLifecycleAware
import xyz.sattar.javid.proqueue.core.ui.components.EmptyState
import xyz.sattar.javid.proqueue.core.ui.components.SectionTabs
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.core.utils.formatPhoneNumberForAction
import xyz.sattar.javid.proqueue.core.utils.openPhoneDial
import xyz.sattar.javid.proqueue.core.utils.openSms
import xyz.sattar.javid.proqueue.core.utils.openTelegram
import xyz.sattar.javid.proqueue.core.utils.openWhatsApp
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import xyz.sattar.javid.proqueue.domain.model.Message
import xyz.sattar.javid.proqueue.domain.model.Visitor
import xyz.sattar.javid.proqueue.feature.lastVisitors.StatusBadge

@Composable
fun VisitorDetailsScreen(
    visitorId: Long,
    viewModel: VisitorDetailsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCreateAppointment: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(visitorId) {
        viewModel.sendIntent(VisitorDetailsIntent.LoadVisitorDetails(visitorId))
    }

    HandleEffects(
        events = viewModel.events,
        onNavigateBack = onNavigateBack
    )

    VisitorDetailsScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToCreateAppointment = { onNavigateToCreateAppointment(visitorId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitorDetailsScreenContent(
    uiState: VisitorDetailsState,
    snackbarHostState: SnackbarHostState,
    onIntent: (VisitorDetailsIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCreateAppointment: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.visitor_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.visitor != null) {
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }

            val listState = rememberLazyListState()
            var selectedTabIndex by remember { mutableStateOf(0) }

            val maxHeight = 250.dp
            val minHeight = 0.dp
            val density = LocalDensity.current
            val collapseRangePx = with(density) { (maxHeight - minHeight).toPx() }
            var headerOffset by remember { mutableStateOf(0f) }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        val delta = available.y
                        val newOffset = (headerOffset - delta).coerceIn(0f, collapseRangePx)
                        val consumed = headerOffset - newOffset
                        headerOffset = newOffset
                        return Offset(0f, consumed)
                    }
                }
            }
            val headerHeight = lerp(maxHeight, minHeight, (headerOffset / collapseRangePx))
            val contentAlpha = 1f - (headerOffset / collapseRangePx)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .nestedScroll(nestedScrollConnection)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(
                            animationSpec = tween(durationMillis = 500, delayMillis = 300)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(headerHeight)
                                .padding(horizontal = 16.dp)
                                .alpha(contentAlpha),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            VisitorInfoHeader(uiState.visitor)
                            Spacer(modifier = Modifier.height(16.dp))
                            CommunicationSection(uiState.visitor)
                        }
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(
                            animationSpec = tween(durationMillis = 500, delayMillis = 200)
                        )
                    ) {
                        SectionTabs(
                            labels = listOf(
                                stringResource(Res.string.messages_tab),
                                stringResource(Res.string.appointments_tab)
                            ),
                            selectedIndex = selectedTabIndex,
                            onSelected = { selectedTabIndex = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (selectedTabIndex == 0) {
                            if (uiState.messages.isEmpty()) {
                                item {
                                    EmptyState(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        icon = Icons.Default.Message,
                                        title = stringResource(Res.string.empty_messages_title),
                                        subtitle = stringResource(Res.string.empty_messages_subtitle)
                                    )
                                }
                            } else {
                                items(uiState.messages) { message ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = message.messageType,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = DateTimeUtils.formatDate(message.sentAt),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        } else {
                            if (uiState.appointments.isEmpty()) {
                                item {
                                    EmptyState(
                                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                                        icon = Icons.Default.EventNote,
                                        title = "برای این مخاطب در این کسب‌وکار نوبتی ثبت نشده",
                                        subtitle = "از صفحه ایجاد نوبت می‌توانید نوبت جدید ثبت کنید"
                                    )
                                }
                            } else {
                                items(uiState.appointments) { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = DateTimeUtils.formatDate(item.appointment.appointmentDate),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = DateTimeUtils.formatTime(item.appointment.appointmentDate),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${stringResource(Res.string.business_name)}: ${item.business.title}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            StatusBadge(status = item.appointment.status, overdue = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VisitorInfoHeader(visitor: Visitor) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = visitor.fullName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = visitor.phoneNumber,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CommunicationSection(visitor: Visitor) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.contact_options),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CommunicationButton(
                icon = Icons.Default.Call,
                label = "تماس",
                onClick = { openPhoneDial(visitor.phoneNumber) }
            )
            CommunicationButton(
                icon = Icons.Default.Message,
                label = "پیامک",
                onClick = { openSms(formatPhoneNumberForAction(visitor.phoneNumber)) }
            )
            CommunicationButton(
                icon = Res.drawable.whatsapp,
                label = "واتساپ",
                onClick = { openWhatsApp(formatPhoneNumberForAction(visitor.phoneNumber)) }
            )
            CommunicationButton(
                icon = Icons.Default.Send, // Placeholder for Telegram
                label = "تلگرام",
                onClick = { openTelegram(formatPhoneNumberForAction(visitor.phoneNumber)) }
            )
        }
    }
}

@Composable
fun CommunicationButton(
    icon: Any,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            when (icon) {
                is ImageVector -> Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
                is org.jetbrains.compose.resources.DrawableResource -> Icon(painterResource(icon), contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                else -> Icon(Icons.Default.Send, contentDescription = label) // Fallback
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun HistorySection(appointments: List<AppointmentWithDetails>, messages: List<Message>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("ارسال پیام", "نوبت‌ها")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "سوابق",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        SectionTabs(
            labels = tabs,
            selectedIndex = selectedTabIndex,
            onSelected = { selectedTabIndex = it },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (selectedTabIndex) {
            0 -> MessagesList(messages)
            1 -> AppointmentsList(appointments)
        }
    }
}

@Composable
fun MessagesList(messages: List<Message>) {
    if (messages.isEmpty()) {
        EmptyState(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            icon = Icons.Default.Message,
            title = "هنوز پیامی برای این مخاطب ثبت نشده",
            subtitle = "از گزینه‌های ارتباطی بالا می‌توانید پیام ارسال کنید"
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = message.messageType,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = DateTimeUtils.formatDate(message.sentAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentsList(appointments: List<AppointmentWithDetails>) {
    if (appointments.isEmpty()) {
        EmptyState(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            icon = Icons.Default.EventNote,
            title = "برای این مخاطب در این کسب‌وکار نوبتی ثبت نشده",
            subtitle = "از صفحه ایجاد نوبت می‌توانید نوبت جدید ثبت کنید"
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(appointments) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = DateTimeUtils.formatDate(item.appointment.appointmentDate),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = DateTimeUtils.formatTime(item.appointment.appointmentDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "کسب‌وکار: ${item.business.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(status = item.appointment.status, overdue = false)
                    }
                }
            }
        }
    }
}


@Composable
fun HandleEffects(
    events: Flow<VisitorDetailsEvent>,
    onNavigateBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            VisitorDetailsEvent.NavigateBack -> onNavigateBack()
        }
    }
}
