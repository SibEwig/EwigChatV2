package com.sibewig.ewigchatv2.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                state = Lifecycle.State.STARTED
            ) {
                viewModel.authState
                    .collect {
                        val navController = findNavController()
                        if (navController.currentDestination?.id != R.id.splashFragment) return@collect
                        when (it) {
                            is AuthState.Authorized -> {
                                navController.navigate(
                                    R.id.action_splashFragment_to_chatsFragment
                                )
                            }

                            AuthState.Unauthorized -> {
                                navController.navigate(
                                    R.id.action_splashFragment_to_authFragment
                                )
                            }

                            AuthState.Initial -> {}
                        }
                    }
            }
        }
    }
}
