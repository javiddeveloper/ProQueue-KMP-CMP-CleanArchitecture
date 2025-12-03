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
import xyz.sattar.javid.proqueue.domain.usecase.LoadBusinessUseCase
import xyz.sattar.javid.proqueue.domain.usecase.VisitorUpsertUseCase
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessState
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessViewModel
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorState
import xyz.sattar.javid.proqueue.feature.createVisitor.CreateVisitorViewModel

val appModule: Module = module {
    single { get<AppDatabase>().businessDao() }
    single<BusinessRepository> { BusinessRepositoryImpl(get()) }
    single<VisitorRepository> { VisitorRepositoryImpl(get()) }
    
    factory { BusinessUpsertUseCase(get()) }
    factory { LoadBusinessUseCase(get()) }
    factory { VisitorUpsertUseCase(get()) }
    
    factory { CreateBusinessState() }
    viewModel { CreateBusinessViewModel(
        get(),
        get(),
        get()
    ) }
    
    factory { CreateVisitorState() }
    viewModel { CreateVisitorViewModel(
        get(),
        get()
    ) }
}