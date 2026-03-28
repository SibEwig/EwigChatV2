package com.sibewig.ewigchatv2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sibewig.ewigchatv2.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {
    private var _binding: FragmentLaunchBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentLaunchBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLaunchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}