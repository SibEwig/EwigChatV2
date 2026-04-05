package com.sibewig.ewigchatv2.presentation.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sibewig.ewigchatv2.databinding.FragmentChatsBinding
import com.sibewig.ewigchatv2.presentation.adapters.ChatAdapter
import com.sibewig.ewigchatv2.presentation.chats.model.ChatsEvent
import com.sibewig.ewigchatv2.presentation.chats.model.ChatsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentChatsBinding == null")

    private val viewModel: ChatsViewModel by viewModels()

    private val adapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupWindowInsets()
        setupClickListeners()
        collectUiState()
        collectEvents()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top + (8 * resources.displayMetrics.density).toInt())
            insets
        }
    }

    private fun setupClickListeners() {
        adapter.onChatClickListener = {
            val direction = ChatsFragmentDirections.actionChatsFragmentToChatFragment(it)
            findNavController().navigate(direction)
        }
        binding.buttonStartChat.setOnClickListener {
            StartChatDialogFragment().show(childFragmentManager, StartChatDialogFragment.TAG)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewChats.layoutManager = layoutManager
        binding.recyclerViewChats.adapter = adapter
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatsState.collect { state ->
                    when (state) {
                        ChatsState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is ChatsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.submitList(state.chats)
                        }

                        is ChatsState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                state.msgRes,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        ChatsState.Initial -> Unit
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
                        is ChatsEvent.OpenChat -> {
                            val direction = ChatsFragmentDirections
                                .actionChatsFragmentToChatFragment(event.chatId, false)
                            findNavController().navigate(direction)
                        }

                        else -> Unit
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