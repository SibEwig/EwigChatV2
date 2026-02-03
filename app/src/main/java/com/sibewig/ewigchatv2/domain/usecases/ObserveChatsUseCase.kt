package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.Chat
import com.sibewig.ewigchatv2.domain.ChatRepository
import com.sibewig.ewigchatv2.domain.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatsUseCase @Inject constructor(private val repository: ChatRepository) {

    operator fun invoke(uid: String): Flow<List<Chat>> {
        return repository.observeChats(uid)
    }
}