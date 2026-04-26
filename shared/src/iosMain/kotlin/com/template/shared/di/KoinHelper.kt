package com.template.shared.di

import com.template.shared.presentation.AuthViewModel
import com.template.shared.presentation.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinHelper : KoinComponent {
    private val mainViewModel: MainViewModel by inject()
    private val authViewModel: AuthViewModel by inject()
    
    fun getMainViewModel(): MainViewModel = mainViewModel
    fun getAuthViewModel(): AuthViewModel = authViewModel
}

class FlowAdapter<T : Any>(
    private val scope: CoroutineScope,
    private val flow: Flow<T>
) {
    fun subscribe(
        onEach: (T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (Throwable) -> Unit
    ): Job {
        return flow
            .onEach { onEach(it) }
            .launchIn(scope)
    }
}

fun <T : Any> Flow<T>.asAdapter(): FlowAdapter<T> =
    FlowAdapter(CoroutineScope(Dispatchers.Main), this)

fun <T : Any> Flow<T>.asAdapter(scope: CoroutineScope): FlowAdapter<T> =
    FlowAdapter(scope, this)

