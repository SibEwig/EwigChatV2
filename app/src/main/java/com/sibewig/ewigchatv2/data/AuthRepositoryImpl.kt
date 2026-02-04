package com.sibewig.ewigchatv2.data

import com.google.firebase.auth.FirebaseAuth
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth) : AuthRepository {

    override val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(
                if (user == null) {
                    AuthState.Unauthorized
                } else {
                    AuthState.Authorized(user.uid)
                }
            )
        }

        auth.addAuthStateListener(listener)
        val currentUser = auth.currentUser
        trySend(
            if (currentUser == null) {
                AuthState.Unauthorized
            } else {
                AuthState.Authorized(currentUser.uid)
            }
        )

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }
        .distinctUntilChanged()

    override suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun logout() {
        auth.signOut()

    }
}