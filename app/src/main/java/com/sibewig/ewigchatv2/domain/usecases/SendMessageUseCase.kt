package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.ChatRepository
import com.sibewig.ewigchatv2.domain.Message
import com.sibewig.ewigchatv2.domain.OutgoingMessage
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(msg: OutgoingMessage) {
        repository.sendMessage(msg)
    }
}