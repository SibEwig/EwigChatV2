package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.exceptions.UsernameIsTakenException
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import com.sibewig.ewigchatv2.domain.repository.ProfilesRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profilesRepository: ProfilesRepository
) {

    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        displayName: String,
        photoUrl: String?
    ) {

        if (!profilesRepository.isUsernameAvailable(username)) {
            throw UsernameIsTakenException()
        }

        val uid = authRepository.register(
            email = email,
            password = password
        )
        profilesRepository.createProfile(
            uid = uid,
            email = email,
            displayName = displayName,
            avatarUrl = photoUrl,
            username = username
        )
        authRepository.sendVerificationEmail()
        authRepository.logout()
    }
}