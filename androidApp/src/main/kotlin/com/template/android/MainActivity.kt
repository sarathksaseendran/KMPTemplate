package com.template.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.template.android.ui.HomeScreen
import com.template.android.ui.LoginScreen
import com.template.shared.presentation.AuthViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = koinViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)
    
    var showLogin by remember { mutableStateOf(!isLoggedIn) }
    
    // Sync local state with ViewModel's isLoggedIn
    LaunchedEffect(isLoggedIn) {
        showLogin = !isLoggedIn
    }

    if (showLogin) {
        LoginScreen(
            viewModel = authViewModel,
            onLoginSuccess = { showLogin = false }
        )
    } else {
        HomeScreen(
            viewModel = authViewModel,
            onLogout = { showLogin = true }
        )
    }
}
