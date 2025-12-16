package xyz.sattar.javid.proqueue.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import xyz.sattar.javid.proqueue.data.localDataSource.AppDatabase
import xyz.sattar.javid.proqueue.data.repository.AppointmentRepositoryImpl
import xyz.sattar.javid.proqueue.data.repository.BusinessRepositoryImpl
import xyz.sattar.javid.proqueue.data.repository.MessageRepositoryImpl
import xyz.sattar.javid.proqueue.data.repository.VisitorRepositoryImpl
import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.MessageRepository
import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase
import xyz.sattar.javid.proqueue.domain.usecase.CheckAppointmentConflictUseCase
import xyz.sattar.javid.proqueue.domain.usecase.CreateAppointmentUseCase
import xyz.sattar.javid.proqueue.domain.usecase.DeleteBusinessUseCase
import xyz.sattar.javid.proqueue.domain.usecase.DeleteVisitorUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetAllVisitorsUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetTodayAppointmentsUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetTodayStatsUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetVisitorByIdUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetWaitingQueueUseCase
import xyz.sattar.javid.proqueue.domain.usecase.LoadAllBusinessUseCase
import xyz.sattar.javid.proqueue.domain.usecase.MarkAppointmentCompletedUseCase
import xyz.sattar.javid.proqueue.domain.usecase.MarkAppointmentNoShowUseCase
import xyz.sattar.javid.proqueue.domain.usecase.RemoveAppointmentUseCase
import xyz.sattar.javid.proqueue.domain.usecase.SendMessageUseCase
import xyz.sattar.javid.proqueue.domain.usecase.VisitorUpsertUseCase
import xyz.sattar.javid.proqueue.feature.businessList.BusinessListViewModel
import xyz.sattar.javid.proqueue.feature.createAppointment.CreateAppointmentViewModel
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessState
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessViewModel
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorState
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorViewModel
import xyz.sattar.javid.proqueue.feature.home.HomeViewModel
import xyz.sattar.javid.proqueue.feature.lastVisitors.LastVisitorsViewModel
import xyz.sattar.javid.proqueue.feature.settings.SettingsViewModel

import xyz.sattar.javid.proqueue.feature.visitorSelection.VisitorSelectionViewModel
import xyz.sattar.javid.proqueue.domain.usecase.GetAppointmentByIdUseCase
import xyz.sattar.javid.proqueue.domain.usecase.UpdateAppointmentUseCase

val appModule: Module = module {
    // DAOs
    single { get<AppDatabase>().businessDao() }
    single { get<AppDatabase>().visitorDao() }
    single { get<AppDatabase>().appointmentDao() }
    single { get<AppDatabase>().messageDao() }

    // Repositories
    single<BusinessRepository> { BusinessRepositoryImpl(get()) }
    single<VisitorRepository> { VisitorRepositoryImpl(get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get(), get(), get()) }
    single<MessageRepository> { MessageRepositoryImpl(get()) }

    // Business UseCases
    factory { LoadAllBusinessUseCase(get()) }
    factory { DeleteBusinessUseCase(get()) }
    factory { BusinessUpsertUseCase(get()) }


    // Appointment UseCases
    factory { GetWaitingQueueUseCase(get()) }
    factory { GetTodayAppointmentsUseCase(get()) }
    factory { CreateAppointmentUseCase(get()) }
    factory { RemoveAppointmentUseCase(get()) }
    factory { MarkAppointmentCompletedUseCase(get()) }
    factory { MarkAppointmentNoShowUseCase(get()) }
    factory { GetTodayStatsUseCase(get()) }
    factory { GetAppointmentByIdUseCase(get()) }
    factory { UpdateAppointmentUseCase(get()) }
    factory { CheckAppointmentConflictUseCase(get()) }

    // Message UseCases
    factory { SendMessageUseCase(get()) }

    // Visitor UseCases
    factory { VisitorUpsertUseCase(get()) }
    factory { GetAllVisitorsUseCase(get()) }
    factory { GetVisitorByIdUseCase(get()) }
    factory { DeleteVisitorUseCase(get(), get()) }

    // States
    factory { CreateBusinessState() }
    factory { CreateVisitorState() }

    // ViewModels
    viewModel { CreateBusinessViewModel(get(), get(), get()) }
    viewModel { CreateVisitorViewModel(get(), get(), get()) }
    viewModel { CreateAppointmentViewModel(get(), get(), get(), get(), get()) }
    viewModel { 
        HomeViewModel(
            get(), // GetWaitingQueueUseCase
            get(), // GetTodayStatsUseCase
            get(), // RemoveAppointmentUseCase
            get(), // MarkAppointmentCompletedUseCase
            get(), // MarkAppointmentNoShowUseCase
            get()  // SendMessageUseCase
        ) 
    }
    viewModel { 
        LastVisitorsViewModel(
            get(), // GetTodayAppointmentsUseCase
            get(), // RemoveAppointmentUseCase
            get(), // MarkAppointmentCompletedUseCase
            get()  // MarkAppointmentNoShowUseCase
        )
    }
    viewModel { VisitorSelectionViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { BusinessListViewModel(get()) }
}
