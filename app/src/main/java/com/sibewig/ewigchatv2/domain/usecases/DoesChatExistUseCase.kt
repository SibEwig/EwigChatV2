package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import javax.inject.Inject

class DoesChatExistUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    suspend operator fun invoke(chatId: String): Boolean {
        return chatRepository.isChatCreated(chatId)
    }
}