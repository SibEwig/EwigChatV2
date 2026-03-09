package com.sibewig.ewigchatv2.presentation.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.FragmentRegistrationBinding
import com.sibewig.ewigchatv2.presentation.registration.model.RegistrationState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding
        get() = _binding
            ?: throw RuntimeException("FragmentRegistrationBinding == null")

    private val viewModel: RegistrationViewModel by viewModels()

    private var unlockOnNextEdit: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWindowInsets()
        collectUiState()
        setUpInputListeners()
        setUpClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registrationState.collect { state ->
                    with(binding) {
                        when(state) {
                            is RegistrationState.Error -> {
                                progressBar.visibility = View.GONE
                                buttonRegister.isEnabled = true
                                textViewError.text = state.error
                                textViewError.visibility = View.VISIBLE
                            }
                            is RegistrationState.InputError -> {
                                buttonRegister.isEnabled = false
                                unlockOnNextEdit = true
                                progressBar.visibility = View.GONE

                                usernameInputLayout.error = state.usernameError
                                emailInputLayout.error = state.emailError
                                passwordInputLayout.error = state.passwordError
                                repeatPasswordInputLayout.error = state.repeatPasswordError
                                displayNameInputLayout.error = state.displayNameError
                            }
                            is RegistrationState.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.confirm_registration),
                                    Toast.LENGTH_LONG
                                ).show()
                                val navController = findNavController()
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    key = KEY_PREFILL_EMAIL,
                                    value = state.email
                                )
                                navController.popBackStack()
                            }
                            RegistrationState.Loading -> {
                                progressBar.visibility = View.VISIBLE
                                buttonRegister.isEnabled = false
                            }
                            RegistrationState.Initial -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun setUpInputListeners() {
        with(binding) {
            fun onAnyFieldEdited() {
                if (unlockOnNextEdit) {
                    buttonRegister.isEnabled = true
                    unlockOnNextEdit = false
                }
            }
            editTextUsername.doAfterTextChanged {
                onAnyFieldEdited()
                this.usernameInputLayout.error = null
            }
            editTextEmail.doAfterTextChanged {
                onAnyFieldEdited()
                this.emailInputLayout.error = null
            }
            editTextPassword.doAfterTextChanged {
                onAnyFieldEdited()
                this.passwordInputLayout.error = null
            }
            editTextRepeatPassword.doAfterTextChanged {
                onAnyFieldEdited()
                this.repeatPasswordInputLayout.error = null
            }
            editTextDisplayName.doAfterTextChanged {
                onAnyFieldEdited()
                this.displayNameInputLayout.error = null
            }
        }
    }

    private fun setUpClickListeners() {
        binding.buttonRegister.setOnClickListener {
            with(binding) {
                val username = editTextUsername.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                val repeatPassword = editTextRepeatPassword.text.toString()
                val displayName = editTextDisplayName.text.toString()
                viewModel.onRegister(
                    username = username,
                    email = email,
                    password = password,
                    repeatPassword = repeatPassword,
                    displayName = displayName
                )
            }

        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            v.updatePadding(
                top = systemBars.top + (8 * resources.displayMetrics.density).toInt(),
                bottom = maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }
    }

    companion object {

        const val KEY_PREFILL_EMAIL = "prefill_email"
    }
}