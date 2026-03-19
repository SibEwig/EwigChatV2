package com.sibewig.ewigchatv2.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sibewig.ewigchatv2.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = when (destination.id) {
                R.id.chatsFragment,
                R.id.profileFragment,
                R.id.settingsFragment -> true
                else -> false
            }
        }
        collectAuthState()

    }

    private fun collectAuthState() {
        val navHost = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
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

