package com.sibewig.ewigchatv2.presentation.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.ActivityMainBinding
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val bottomNavDestinations = setOf(
        R.id.chatsFragment,
        R.id.profileFragment,
        R.id.settingsFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLayout()
        setupNavigation()
        setupInsets()
        collectAuthState()
        collectSettings()
    }

    private fun setupLayout() {
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupNavigation() {
        navController = getNavController()
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = destination.id in bottomNavDestinations
        }
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { authState ->
                    val destId = navController.currentDestination?.id

                    when (authState) {
                        is AuthState.Authorized -> {
                            if (destId.isUnauthorizedDestination()) {
                                navController.navigate(
                                    R.id.chatsFragment,
                                    null,
                                    navOptions {
                                        popUpTo(R.id.launchFragment) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                )
                            }
                        }

                        AuthState.Unauthorized -> {
                            if (destId.isAuthorizedDestination()) {
                                navController.navigate(
                                    R.id.authFragment,
                                    null,
                                    navOptions {
                                        popUpTo(R.id.launchFragment) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                )
                            }
                        }

                        AuthState.Initial -> Unit
                    }
                }
            }
        }
    }

    private fun Int?.isAuthorizedDestination(): Boolean {
        return this == R.id.chatsFragment ||
                this == R.id.chatFragment ||
                this == R.id.profileFragment ||
                this == R.id.settingsFragment
    }

    private fun Int?.isUnauthorizedDestination(): Boolean {
        return this == R.id.launchFragment ||
                this == R.id.authFragment ||
                this == R.id.registrationFragment
    }

    private fun collectSettings() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    applyAppLanguage(settings.appLanguage)
                    applyThemeMode(settings.themeMode)
                }
            }
        }
    }

    private fun applyAppLanguage(language: AppLanguage) {
        val locales = when (language) {
            AppLanguage.RU -> LocaleListCompat.forLanguageTags("ru")
            AppLanguage.EN -> LocaleListCompat.forLanguageTags("en")
        }

        if (AppCompatDelegate.getApplicationLocales() != locales) {
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }

    private fun applyThemeMode(themeMode: ThemeMode) {
        val mode = when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}

