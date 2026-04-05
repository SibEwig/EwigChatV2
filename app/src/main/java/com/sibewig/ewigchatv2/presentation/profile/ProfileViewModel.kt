package com.sibewig.ewigchatv2.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.usecases.GetAuthStateUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetProfileByUidUseCase
import com.sibewig.ewigchatv2.domain.usecases.UpdateProfileUseCase
import com.sibewig.ewigchatv2.presentation.profile.model.ProfileEvent
import com.sibewig.ewigchatv2.presentation.profile.model.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileByUidUseCase: GetProfileByUidUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState(isLoading = true))
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getAuthStateUseCase().collect { state ->
                when (state) {
                    is AuthState.Authorized -> {
                        try {
                            val profile = getProfileByUidUseCase(state.userID)
                            if (profile == null) {
                                _events.emit(
                                    ProfileEvent.ShowError(
                                        R.string.error_profile_load_failed
                                    )
                                )
                                return@collect
                            }
                            _state.emit(ProfileState(profile = profile))
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            _events.emit(
                                ProfileEvent.ShowError(
                                    R.string.error_profile_load_failed
                                )
                            )
                        }
                    }

                    AuthState.Unauthorized -> {
                        _events.emit(
                            ProfileEvent.ShowError(
                                R.string.error_not_authorized
                            )
                        )
                    }

                    AuthState.Initial -> Unit
                }
            }
        }
    }

    fun onEdit() {
        _state.value = state.value.copy(isEditMode = true)
    }

    fun onDiscardChanges() {
        _state.value = _state.value.copy(isEditMode = false)
    }

    fun onSaveChanges(
        displayName: String,
        about: String
    ) {
        if (_state.value.profile?.displayName == displayName &&
            _state.value.profile?.about == about
        ) {
            _state.value = _state.value.copy(isEditMode = false)
            return
        }
        viewModelScope.launch {
            val profile = _state.value.profile ?: return@launch
            _state.emit(_state.value.copy(isSaving = true))
            try {
                val editedProfile = profile.copy(
                    displayName = displayName,
                    about = about
                )
                updateProfileUseCase(editedProfile)
                _state.emit(ProfileState(profile = editedProfile))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _events.emit(ProfileEvent.ShowError(R.string.error_profile_edit_failed))
                _state.emit(ProfileState(profile = profile))
            }
        }
    }
}