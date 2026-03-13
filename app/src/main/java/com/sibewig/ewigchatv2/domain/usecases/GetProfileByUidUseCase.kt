package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.Profile
import com.sibewig.ewigchatv2.domain.repository.ProfilesRepository
import javax.inject.Inject

class GetProfileByUidUseCase@Inject constructor(
    private val profilesRepository: ProfilesRepository
) {

    suspend operator fun invoke(uid: String): Profile? {
        return profilesRepository.getProfileByUid(uid)
    }
}