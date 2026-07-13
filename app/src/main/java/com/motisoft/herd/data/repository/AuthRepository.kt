package com.motisoft.herd.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {
    val currentUserId: String?
        get() = supabase.auth.currentUserOrNull()?.id

    val currentUserEmail: String?
        get() = supabase.auth.currentUserOrNull()?.email

    val isSignedIn: Boolean
        get() = supabase.auth.currentUserOrNull() != null

    // Suspends until the SDK finishes loading any session saved from a previous
    // launch, so callers don't have to guess whether currentUserOrNull() is ready yet.
    suspend fun awaitInitialization() {
        supabase.auth.awaitInitialization()
    }

    suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun updatePassword(newPassword: String) {
        supabase.auth.updateUser {
            password = newPassword
        }
    }
}
