package xyz.sattar.javid.proqueue.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import xyz.sattar.javid.proqueue.data.localDataSource.AppDatabase
import xyz.sattar.javid.proqueue.data.repository.BusinessRepositoryImpl
import xyz.sattar.javid.proqueue.data.repository.VisitorRepositoryImpl
import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase
import xyz.sattar.javid.proqueue.feature.createBusiness.CreateBusinessViewModel

val appModule: Module = module {

    // Provide businessDao
    single { get<AppDatabase>().businessDao() }
    single<BusinessRepository> { BusinessRepositoryImpl(get()) }
    single<VisitorRepository> { VisitorRepositoryImpl(get()) }
    factory { BusinessUpsertUseCase(get()) }
    viewModel { (handle: SavedStateHandle) ->
        CreateBusinessViewModel(
            handle,
            get(),
            get()
        )
    }
}