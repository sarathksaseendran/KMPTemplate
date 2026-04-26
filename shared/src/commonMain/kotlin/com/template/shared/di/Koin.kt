package com.template.shared.di

import com.russhwolf.settings.Settings
import com.template.shared.data.remote.createHttpClient
import com.template.shared.data.repository.AuthRepositoryImpl
import com.template.shared.data.repository.ItemRepositoryImpl
import com.template.shared.domain.repository.AuthRepository
import com.template.shared.domain.repository.ItemRepository
import com.template.shared.presentation.AuthViewModel
import com.template.shared.presentation.MainViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    single { createHttpClient() }
    single { Settings() }
    single<ItemRepository> { ItemRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { AuthViewModel(get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule)
    }

// iOS helper
fun initKoin() = initKoin {}
