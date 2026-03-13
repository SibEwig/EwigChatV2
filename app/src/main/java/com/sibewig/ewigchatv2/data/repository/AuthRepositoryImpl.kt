package com.sibewig.ewigchatv2.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user == null) {
                trySend(AuthState.Unauthorized)
            } else {
                user.reload().addOnCompleteListener {
                    if (user.isEmailVerified) {
                        trySend(AuthState.Authorized(user.uid))
                    } else {
                        trySend(AuthState.Unauthorized)
                    }
                }
            }
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

    override suspend fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun register(
        email: String,
        password: String
    ): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("User ID is null")
    }

    override suspend fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun isEmailVerified(): Boolean {
        val user = auth.currentUser ?: return false
        user.reload().await()
        return user.isEmailVerified
    }

    override fun logout() {
        auth.signOut()
    }
}