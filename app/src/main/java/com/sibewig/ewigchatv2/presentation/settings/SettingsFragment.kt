package com.sibewig.ewigchatv2.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.databinding.FragmentSettingsBinding
import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentSettingsBinding == null")

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var languageAdapter: ArrayAdapter<AppLanguage>
    private lateinit var themeAdapter: ArrayAdapter<ThemeMode>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageSpinner()
        setupThemeSpinner()
        setupClickListeners()
        collectSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.buttonLogout.setOnClickListener {
            viewModel.onLogout()
        }
        binding.imageViewLanguageArrow.setOnClickListener {
            binding.spinnerLanguage.performClick()
        }
        binding.imageViewThemeArrow.setOnClickListener {
            binding.spinnerTheme.performClick()
        }
    }

    private fun setupLanguageSpinner() {
        languageAdapter = createEnumSpinnerAdapter(AppLanguage.entries)

        binding.spinnerLanguage.adapter = languageAdapter
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = AppLanguage.entries[position]
                if (viewModel.settings.value.appLanguage != selectedLanguage) {
                    viewModel.onAppLanguageChanged(selectedLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupThemeSpinner() {
        themeAdapter = createEnumSpinnerAdapter(ThemeMode.entries)

        binding.spinnerTheme.adapter = themeAdapter
        binding.spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedTheme = ThemeMode.entries[position]
                if (viewModel.settings.value.themeMode != selectedTheme) {
                    viewModel.onThemeModeChanged(selectedTheme)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun collectSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    updateSpinnerSelection(
                        spinner = binding.spinnerLanguage,
                        targetPosition = AppLanguage.entries.indexOf(settings.appLanguage)
                    )
                    updateSpinnerSelection(
                        spinner = binding.spinnerTheme,
                        targetPosition = ThemeMode.entries.indexOf(settings.themeMode)
                    )
                }
            }
        }
    }

    private fun updateSpinnerSelection(spinner: Spinner, targetPosition: Int) {
        if (spinner.selectedItemPosition != targetPosition && targetPosition >= 0) {
            spinner.setSelection(targetPosition, false)
        }
    }

    private fun <T> createEnumSpinnerAdapter(items: List<T>): ArrayAdapter<T> {
        return object : ArrayAdapter<T>(
            requireContext(),
            R.layout.item_spinner_selected,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(
                    R.layout.item_spinner_selected,
                    parent,
                    false
                )
                bindText(view as TextView, getItem(position))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(
                    R.layout.item_spinner_dropdown,
                    parent,
                    false
                )
                bindText(view as TextView, getItem(position))
                return view
            }

            private fun bindText(textView: TextView, item: T?) {
                val textRes = when (item) {
                    is AppLanguage -> item.toTitleRes()
                    is ThemeMode -> item.toTitleRes()
                    else -> error("Unsupported spinner item type: $item")
                }
                textView.text = getString(textRes)
            }
        }
    }

    private fun AppLanguage.toTitleRes(): Int {
        return when (this) {
            AppLanguage.RU -> R.string.language_russian
            AppLanguage.EN -> R.string.language_english
        }
    }

    private fun ThemeMode.toTitleRes(): Int {
        return when (this) {
            ThemeMode.LIGHT -> R.string.theme_light
            ThemeMode.DARK -> R.string.theme_dark
            ThemeMode.SYSTEM -> R.string.theme_system
        }
    }
}