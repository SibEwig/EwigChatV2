package com.sibewig.ewigchatv2.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.R.attr.colorOnSurface
import com.google.android.material.R.attr.colorOnSurfaceVariant
import com.google.android.material.color.MaterialColors
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.FragmentProfileBinding
import com.sibewig.ewigchatv2.domain.entity.Profile
import com.sibewig.ewigchatv2.presentation.profile.model.ProfileEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentProfileBinding == null")

    private val viewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        collectUiState()
        collectEvents()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(state) {
                        with(binding) {
                            setupBaseState()
                            if (isSaving || isLoading) {
                                buttonSave.isEnabled = false
                                buttonEdit.isEnabled = false
                                buttonCancel.isEnabled = false
                                progressBar.visibility = View.VISIBLE
                            }
                            if (isEditMode) {
                                setupEditState()
                            } else {
                                val profile = profile ?: return@collect
                                setupViewState(profile)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ProfileEvent.ShowError -> {
                            Toast.makeText(
                                requireContext(),
                                event.msgRes,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupViewState(profile: Profile) {
        with(binding) {
            editTextAbout.setHint(null)
            buttonEdit.visibility = View.VISIBLE
            buttonEdit.isEnabled = true
            editTextDisplayName.setText(profile.displayName)
            textViewUsername.text =
                getString(R.string.username_format, profile.username)
            textViewEmail.text = profile.email
            editTextAbout.setText(profile.about)
        }
    }

    private fun setupEditState() {
        with(binding) {
            buttonEdit.visibility = View.GONE
            buttonSave.visibility = View.VISIBLE
            buttonSave.isEnabled = true
            buttonCancel.visibility = View.VISIBLE
            buttonCancel.isEnabled = true

            editTextDisplayName.isEnabled = true
            editTextDisplayName.requestFocus()
            editTextDisplayName.setBackgroundResource(R.drawable.bg_editable)
            editTextDisplayName.setTextColor(
                MaterialColors.getColor(
                    editTextDisplayName,
                    colorOnSurface
                )
            )

            editTextAbout.setHint(R.string.hint_about)
            editTextAbout.isEnabled = true
            editTextAbout.setBackgroundResource(R.drawable.bg_editable)
            editTextAbout.setTextColor(
                MaterialColors.getColor(
                    editTextAbout,
                    colorOnSurface
                )
            )
        }
    }

    private fun setupBaseState() {
        with(binding) {
            progressBar.visibility = View.GONE
            buttonSave.visibility = View.GONE
            buttonCancel.visibility = View.GONE
            buttonEdit.isEnabled = false

            editTextDisplayName.isEnabled = false
            editTextDisplayName.background = null
            editTextDisplayName.setTextColor(
                MaterialColors.getColor(
                    editTextDisplayName,
                    colorOnSurfaceVariant
                )
            )

            editTextAbout.isEnabled = false
            editTextAbout.background = null
            editTextAbout.setTextColor(
                MaterialColors.getColor(
                    editTextAbout,
                    colorOnSurfaceVariant
                )
            )
        }
    }

    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            val updatedDisplayName = binding.editTextDisplayName.text.toString()
            val updatedAbout = binding.editTextAbout.text.toString()
            viewModel.onSaveChanges(updatedDisplayName, updatedAbout)
        }
        binding.buttonCancel.setOnClickListener {
            viewModel.onDiscardChanges()
        }
        binding.buttonEdit.setOnClickListener {
            viewModel.onEdit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}