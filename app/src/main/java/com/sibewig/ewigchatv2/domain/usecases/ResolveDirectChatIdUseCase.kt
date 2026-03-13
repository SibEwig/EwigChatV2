package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.exceptions.CannotStartChatWithYourselfException
import com.sibewig.ewigchatv2.domain.exceptions.ProfileNotFoundException
import com.sibewig.ewigchatv2.domain.exceptions.UnauthorizedException
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import com.sibewig.ewigchatv2.domain.repository.ProfilesRepository
import javax.inject.Inject

class ResolveDirectChatIdUseCase @Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(username: String): String {
        val targetUser = profilesRepository.getProfileByUsername(username)
            ?: throw ProfileNotFoundException()
        val myUid = authRepository.getCurrentUserUid()
            ?: throw UnauthorizedException()
        if (targetUser.id == myUid) {
            throw CannotStartChatWithYourselfException()
        }
        return "$myUid$CHAT_ID_DELIMITER${targetUser.id}"
    }

    companion object {

        private const val CHAT_ID_DELIMITER = "_"
    }
}