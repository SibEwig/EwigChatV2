package com.sibewig.ewigchatv2.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.ActivityMainBinding
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
        setupInsets()
        collectAuthState()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { view, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updatePadding(bottom = navBars.bottom)

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragment) { view, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            view.updatePadding(
                top = 0,
                bottom = if (imeVisible) ime.bottom else navBars.bottom
            )

            insets
        }
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

