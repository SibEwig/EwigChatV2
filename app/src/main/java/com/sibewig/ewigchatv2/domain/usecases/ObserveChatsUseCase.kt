package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatsUseCase @Inject constructor(private val repository: ChatRepository) {

    operator fun invoke(uid: String): Flow<List<Chat>> {
        return repository.observeChats(uid)
    }
}