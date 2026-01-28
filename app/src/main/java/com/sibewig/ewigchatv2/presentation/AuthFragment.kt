package com.sibewig.ewigchatv2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.FragmentAuthBinding
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private var _binding: FragmentAuthBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentAuthBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        collectAuthState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { authState ->
                    val navController = findNavController()
                    if (navController.currentDestination?.id != R.id.authFragment) return@collect
                    when (authState) {
                        is AuthState.Authorized -> {
                            navController.navigate(R.id.action_authFragment_to_chatsFragment)
                        }

                        is AuthState.Unauthorized -> {

                        }

                        AuthState.Initial -> {

                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            buttonRegister.setOnClickListener {
                val email = editTextEmail.text.trim().toString()
                val password = editTextPassword.text.trim().toString()
                viewModel.onRegister(email, password)
            }
            buttonLogin.setOnClickListener {
                val email = editTextEmail.text.trim().toString()
                val password = editTextPassword.text.trim().toString()
                viewModel.onLogin(email, password)
            }
        }
    }
}