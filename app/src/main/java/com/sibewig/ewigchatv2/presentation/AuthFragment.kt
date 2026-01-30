package com.sibewig.ewigchatv2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sibewig.ewigchatv2.databinding.FragmentAuthBinding
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectAuthScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authScreenState.collect { authScreenState ->
                    with(binding) {
                        if (authScreenState.isLoading) {
                            progressBar.visibility = View.VISIBLE
                            buttonLogin.isEnabled = false
                            buttonRegister.isEnabled = false
                        } else {
                            progressBar.visibility = View.GONE
                            buttonLogin.isEnabled = true
                            buttonRegister.isEnabled = true
                        }
                        textViewError.text = authScreenState.error ?: ""
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