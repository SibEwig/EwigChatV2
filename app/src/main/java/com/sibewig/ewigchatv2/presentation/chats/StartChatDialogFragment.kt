package com.sibewig.ewigchatv2.presentation.chats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sibewig.ewigchatv2.databinding.FragmentStartChatDialogBinding
import com.sibewig.ewigchatv2.presentation.chats.model.ChatsEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartChatDialogFragment : DialogFragment() {

    private var _binding: FragmentStartChatDialogBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentStartChatDialogBinding == null")

    private val viewModel: ChatsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { requireParentFragment().defaultViewModelProviderFactory }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartChatDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonConfirm.setOnClickListener {
            binding.usernameInputLayout.error = null
            val username = binding.editTextUsername.text.toString()
            viewModel.startChatWithUser(username)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ChatsEvent.ShowStartChatError -> {
                            binding.usernameInputLayout.error = getString(event.messageRes)
                        }

                        else -> {
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "StartChatDialog"
    }
}