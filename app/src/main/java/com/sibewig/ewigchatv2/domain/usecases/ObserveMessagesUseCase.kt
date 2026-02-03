package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.ChatRepository
import com.sibewig.ewigchatv2.domain.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(private val repository: ChatRepository) {

    operator fun invoke(chatID: String): Flow<List<Message>> {
        return repository.observeMessages(chatID)
    }
}