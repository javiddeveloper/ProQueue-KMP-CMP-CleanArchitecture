package xyz.sattar.javid.proqueue.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import xyz.sattar.javid.proqueue.data.localDataSource.AppDatabase
import xyz.sattar.javid.proqueue.data.repository.BusinessRepositoryImpl
import xyz.sattar.javid.proqueue.data.repository.VisitorRepositoryImpl
import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase
import xyz.sattar.javid.proqueue.domain.usecase.DeleteBusinessUseCase
import xyz.sattar.javid.proqueue.domain.usecase.LoadAllBusinessUseCase
import xyz.sattar.javid.proqueue.domain.usecase.VisitorUpsertUseCase
import xyz.sattar.javid.proqueue.feature.businessList.BusinessListViewModel
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessState
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessViewModel
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorState
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorViewModel
import xyz.sattar.javid.proqueue.feature.home.HomeViewModel
import xyz.sattar.javid.proqueue.feature.lastVisitors.LastVisitorsViewModel
import xyz.sattar.javid.proqueue.feature.settings.SettingsViewModel

val appModule: Module = module {
    single { get<AppDatabase>().businessDao() }
    single<BusinessRepository> { BusinessRepositoryImpl(get()) }
    single<VisitorRepository> { VisitorRepositoryImpl(get()) }
    
    factory { BusinessUpsertUseCase(get()) }
    factory { LoadAllBusinessUseCase(get()) }
    factory { DeleteBusinessUseCase(get()) }
    factory { VisitorUpsertUseCase(get()) }
    
    factory { CreateBusinessState() }
    viewModel { CreateBusinessViewModel(
        get(),
        get()
    ) }
    
    factory { CreateVisitorState() }
    viewModel { CreateVisitorViewModel(
        get(),
        get()
    ) }
    
    // Main tab ViewModels
    viewModel { HomeViewModel() }
    viewModel { LastVisitorsViewModel() }
    viewModel { SettingsViewModel(get()) }
    
    viewModel { BusinessListViewModel(get()) }
}