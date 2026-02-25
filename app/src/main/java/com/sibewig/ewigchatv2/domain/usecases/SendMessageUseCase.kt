package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(chatId: String, msg: String) {
        repository.sendMessage(chatId, msg)
    }
}