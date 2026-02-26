package com.sibewig.ewigchatv2.presentation.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sibewig.ewigchatv2.databinding.FragmentChatBinding
import com.sibewig.ewigchatv2.presentation.adapters.MessageAdapter
import com.sibewig.ewigchatv2.presentation.chat.model.ChatState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentChatBinding == null")

    private val viewModel: ChatViewModel by viewModels()

    private val adapter = MessageAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()
        setupRecyclerView()
        collectUiState()
        binding.buttonSendMessage.setOnClickListener {
            val trimmed = binding.editTextMessage.text?.toString()?.trim()
            if (trimmed.isNullOrEmpty()) return@setOnClickListener
            viewModel.sendMessage(trimmed)
            binding.editTextMessage.text?.clear()
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

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewMessages.adapter = adapter
            recyclerViewMessages.layoutManager = LinearLayoutManager(
                requireContext()
            ).apply {
                stackFromEnd = true
            }
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatState.collect {state ->
                    when(state) {
                        is ChatState.Error -> {
                        }
                        ChatState.Initial -> {
                        }
                        ChatState.Loading -> {
                        }
                        is ChatState.Success -> {
                            binding.textViewContactName.text = state.interlocutorName
                            adapter.submitList(state.messages)
                            if (state.messages.isNotEmpty()) {
                                binding.recyclerViewMessages.scrollToPosition(state.messages.lastIndex)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}