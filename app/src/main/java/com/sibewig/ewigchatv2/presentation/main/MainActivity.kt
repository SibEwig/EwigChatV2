package com.sibewig.ewigchatv2.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.NavHostFragment
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
                viewModel.authState
                    .collect { authState ->
                    Log.d("MainActivity", "Current state: $authState")
                    when (authState) {
                        is AuthState.Authorized -> {
                            val destId = navController.currentDestination?.id
                            if (destId == R.id.authFragment) {
                                val options = NavOptions.Builder()
                                    .setPopUpTo(R.id.authFragment, true)
                                    .setLaunchSingleTop(true)
                                    .build()
                                navController.navigate(R.id.chatsFragment, null, options)
                            }
                        }

                        AuthState.Unauthorized -> {
                            val destId = navController.currentDestination?.id
                            if (destId != R.id.authFragment) {
                                val options = NavOptions.Builder()
                                    .setPopUpTo(navController.graph.id, true)
                                    .setLaunchSingleTop(true)
                                    .build()
                                navController.navigate(R.id.authFragment, null, options)
                            }
                        }

                        AuthState.Initial -> {}
                    }
                }
            }
        }
    }
}

