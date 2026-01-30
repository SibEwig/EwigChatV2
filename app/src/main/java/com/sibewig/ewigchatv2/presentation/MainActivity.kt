package com.sibewig.ewigchatv2.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        collectAuthState()
    }

    private fun collectAuthState() {
        val navHost = supportFragmentManager.findFragmentById(
            R.id.nav_host
        ) as NavHostFragment
        val navController = navHost.navController
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { authState ->
                    Log.d("MainActivity", "Current state: $authState")
                    when (authState) {
                        is AuthState.Authorized -> {
                            if (navController.currentDestination?.id == R.id.chatsFragment) return@collect
                            navController.navigate(R.id.chatsFragment)
                        }

                        AuthState.Unauthorized -> {
                            if (navController.currentDestination?.id == R.id.authFragment) return@collect
                            navController.navigate(R.id.authFragment)
                        }

                        AuthState.Initial -> {}
                    }
                }
            }
        }
    }
}

