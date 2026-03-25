package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.Profile
import com.sibewig.ewigchatv2.domain.repository.ProfilesRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfilesRepository
) {

    suspend operator fun invoke(profile: Profile) {
        repository.updateProfile(profile)
    }
}