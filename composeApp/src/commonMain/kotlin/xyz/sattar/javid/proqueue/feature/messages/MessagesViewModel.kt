package xyz.sattar.javid.proqueue.feature.messages

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.prefs.PreferencesManager
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils

class MessagesViewModel :
    BaseViewModel<MessagesState, MessagesState.PartialState, Unit, MessagesIntent>(
        initialState = MessagesState()
    ) {
    override fun handleIntent(intent: MessagesIntent): Flow<MessagesState.PartialState> {
        return when (intent) {
            MessagesIntent.Load -> load()
            is MessagesIntent.UpdateTemplate -> updateTemplate(intent.text)
            is MessagesIntent.InsertToken -> insertToken(intent.token)
            is MessagesIntent.SetReminder -> setReminder(intent.minutes)
            is MessagesIntent.ApplyReadyTemplate -> applyReady(intent.text)
            MessagesIntent.Save -> save()
        }
    }

    private fun load(): Flow<MessagesState.PartialState> = flow {
        val business = BusinessStateHolder.selectedBusiness.value
        if (business == null) {
            emit(MessagesState.PartialState.ShowMessage("کسب‌وکار انتخاب نشده"))
            return@flow
        }
        emit(MessagesState.PartialState.IsLoading(true))
        val templateFlow = PreferencesManager.messageTemplate(business.id)
        val currentTemplate: String? = templateFlow.first()
        val defaultTemplate = currentTemplate ?: defaultTemplates().first()
        emit(MessagesState.PartialState.ApplyBusiness(business.id, business.title))
        emit(MessagesState.PartialState.ApplyTemplate(defaultTemplate))
        emit(MessagesState.PartialState.ApplyPreview(buildPreview(defaultTemplate)))
        emit(MessagesState.PartialState.LoadReadyTemplates(defaultTemplates()))
        emit(MessagesState.PartialState.IsLoading(false))
    }

    private fun updateTemplate(text: String): Flow<MessagesState.PartialState> = flow {
        emit(MessagesState.PartialState.ApplyTemplate(text))
        emit(MessagesState.PartialState.ApplyPreview(buildPreview(text)))
    }

    private fun insertToken(token: String): Flow<MessagesState.PartialState> = flow {
        val appended = (uiState.value.template + token)
        emit(MessagesState.PartialState.ApplyTemplate(appended))
        emit(MessagesState.PartialState.ApplyPreview(buildPreview(appended)))
    }

    private fun setReminder(minutes: Int): Flow<MessagesState.PartialState> = flow {
        emit(MessagesState.PartialState.SetReminder(minutes))
        emit(MessagesState.PartialState.ApplyPreview(buildPreview(uiState.value.template, minutes)))
    }

    private fun applyReady(text: String): Flow<MessagesState.PartialState> = flow {
        emit(MessagesState.PartialState.ApplyTemplate(text))
        emit(MessagesState.PartialState.ApplyPreview(buildPreview(text)))
    }

    private fun save(): Flow<MessagesState.PartialState> = flow {
        val business = BusinessStateHolder.selectedBusiness.value
        if (business == null) {
            emit(MessagesState.PartialState.ShowMessage("کسب‌وکار انتخاب نشده"))
            return@flow
        }
        PreferencesManager.setMessageTemplate(business.id, uiState.value.template)
        emit(MessagesState.PartialState.ShowMessage("تنظیمات ذخیره شد"))
    }

    override fun reduceState(
        currentState: MessagesState,
        partialState: MessagesState.PartialState
    ): MessagesState {
        return when (partialState) {
            is MessagesState.PartialState.IsLoading -> currentState.copy(isLoading = partialState.loading)
            is MessagesState.PartialState.ShowMessage -> currentState.copy(message = partialState.text)
            is MessagesState.PartialState.ApplyBusiness -> currentState.copy(
                businessId = partialState.id,
                businessTitle = partialState.title,
                isLoading = false
            )

            is MessagesState.PartialState.ApplyTemplate -> currentState.copy(template = partialState.text)
            is MessagesState.PartialState.ApplyPreview -> currentState.copy(preview = partialState.text)
            is MessagesState.PartialState.SetReminder -> currentState.copy(reminderMinutes = partialState.minutes)
            is MessagesState.PartialState.LoadReadyTemplates -> currentState.copy(readyTemplates = partialState.list)
        }
    }

    override fun createErrorState(message: String): MessagesState.PartialState =
        MessagesState.PartialState.ShowMessage(message)

    private fun buildPreview(
        template: String,
        minutes: Int = uiState.value.reminderMinutes
    ): String {
        val business = BusinessStateHolder.selectedBusiness.value
        val businessTitle = business?.title ?: "کسب‌وکار شما"
        val address = business?.address ?: "--"
        val duration = "${business?.defaultServiceDuration} دقیقه " ?: "مشخص نشده"
        val visitorName = "سارا"
        val time = DateTimeUtils.formatTimeNow()
        val date = DateTimeUtils.formatMillisDateOnly(
            DateTimeUtils.systemCurrentMilliseconds()
        )
        return template
            .replace("{visitor}", visitorName)
            .replace("{business}", businessTitle)
            .replace("{address}", address)
            .replace("{time}", time)
            .replace("{date}", date)
            .replace("{minutes}", minutes.toString())
            .replace("{duration}", duration)
    }


    private fun defaultTemplates(): List<String> = listOf(
        "با سلام {visitor} عزیز 🌹؛ یادآوری نوبت شما در {business} برای ساعت {time}. مدت زمان خدمت به شما حدود {duration} است. لطفاً تا {minutes} دقیقه دیگر در محل حضور داشته باشید.",
        "سلام {visitor} جان! 👋 منتظر دیدار شما در {business} هستیم. نوبت شما: {time}. زمان خدمت به شما حدود: {duration} دقیقه. ممنون می‌شیم {minutes} دقیقه دیگه تشریف بیارید.",
        "{visitor} گرامی؛ نوبت {business} شما ساعت {time} آغاز می‌شود (مدت خدمت به شما حدود: {duration} دقیقه). لطفا جهت پذیرش، {minutes} دقیقه دیگر مراجعه فرمایید.",
        "درود بر شما {visitor} عزیز؛ زمان نوبت شما در {business}: ساعت {time}. این سرویس حدود {duration} زمان می‌برد. لطفا {minutes} دقیقه دیگر حضور بهم رسانید. 🕒",
        "نوبت دهی {business}: {visitor} عزیز، نوبت شما ساعت {time} است. مدت کار حدود: {duration} . لطفا {minutes} دقیقه دیگر تشریف بیاورید. 📍"
    )
}
