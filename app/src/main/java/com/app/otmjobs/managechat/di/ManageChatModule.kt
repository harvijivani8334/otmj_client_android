package com.app.otmjobs.managechat.di


import com.app.otmjobs.common.utils.CoroutineCallAdapterFactory
import com.app.otmjobs.common.utils.VariantConfig
import com.app.otmjobs.managechat.data.remote.ManageChatInterface
import com.app.otmjobs.managechat.data.repository.ManageChatRepository
import com.app.otmjobs.managechat.data.repository.imp.ManageChatRepositoryImp
import com.app.otmjobs.managechat.ui.viewmodel.ManageChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

var manageChatModule = module {
    // Provide Retrofit
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(VariantConfig.serverBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create<ManageChatInterface>()
    }

    single<ManageChatRepository> { ManageChatRepositoryImp(manageChatInterface = get()) }

    viewModel { ManageChatViewModel(manageChatRepository = get()) }
}