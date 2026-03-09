package com.sibewig.ewigchatv2.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.FragmentAuthBinding
import com.sibewig.ewigchatv2.presentation.registration.RegistrationFragment
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
        collectAuthScreenState()
        observePrefillEmail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectAuthScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authScreenState.collect { state ->
                    with(binding) {

                        val loading = state.isLoading
                        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                        buttonLogin.isEnabled = !loading
                        buttonRegister.isEnabled = !loading

                        val error = state.error
                        if (error.isNullOrBlank()) {
                            textViewError.visibility = View.GONE
                        } else {
                            textViewError.visibility = View.VISIBLE
                            textViewError.text = error
                        }
                    }
                }
            }
        }
    }


    private fun setupClickListeners() {
        with(binding) {
            buttonRegister.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.registrationFragment)
            }
            buttonLogin.setOnClickListener {
                val email = editTextEmail.text?.trim().toString()
                val password = editTextPassword.text?.trim().toString()
                viewModel.onLogin(email, password)
            }
        }
    }

    private fun observePrefillEmail() {
        val navController = findNavController()
        val handle = navController.currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<String>(RegistrationFragment.KEY_PREFILL_EMAIL)
            ?.observe(viewLifecycleOwner) {
                binding.editTextEmail.setText(it)
                binding.editTextPassword.requestFocus()
                handle.remove<String>(RegistrationFragment.KEY_PREFILL_EMAIL)
            }
    }
}