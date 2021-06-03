package com.app.otmjobs.managejob.di


import com.app.otmjobs.common.utils.CoroutineCallAdapterFactory
import com.app.otmjobs.common.utils.VariantConfig
import com.app.otmjobs.managejob.data.remote.ManageJobInterface
import com.app.otmjobs.managejob.data.repository.ManageJobRepository
import com.app.otmjobs.managejob.data.repository.imp.ManageJobRepositoryImp
import com.app.otmjobs.managejob.ui.viewmodel.ManageJobViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

var manageJobModule = module {
    // Provide Retrofit
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(VariantConfig.serverBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create<ManageJobInterface>()
    }

    single<ManageJobRepository> { ManageJobRepositoryImp(manageJobInterface = get()) }

    viewModel { ManageJobViewModel(manageJobRepository = get()) }
}