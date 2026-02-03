package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import com.sibewig.ewigchatv2.domain.entities.OutgoingMessage
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(msg: OutgoingMessage) {
        repository.sendMessage(msg)
    }
}