package com.example.myapplication.data.remote

import android.util.Patterns
import com.example.myapplication.model.City
import com.example.myapplication.model.Role
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.UUID

private data class FirestoreUserDto(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val role: String? = null,
    val city: String? = null,
    val email: String = ""
) {
    fun toDomain(defaultEmail: String?): User {
        val safeEmail = email.ifBlank { defaultEmail.orEmpty() }
        val safeUsername = username.ifBlank { safeEmail.substringBefore('@', missingDelimiterValue = randomAlias()) }
        return User(
            id = id.ifBlank { UUID.randomUUID().toString() },
            name = name.ifBlank { safeUsername.replaceFirstChar { it.titlecase(Locale.getDefault()) } },
            username = safeUsername,
            role = role?.let { runCatching { Role.valueOf(it.uppercase(Locale.ROOT)) }.getOrDefault(Role.USER) } ?: Role.USER,
            city = city?.let { runCatching { City.valueOf(it.uppercase(Locale.ROOT)) }.getOrDefault(City.ARMENIA) } ?: City.ARMENIA,
            email = safeEmail
        )
    }

    private fun randomAlias(): String = "user-${UUID.randomUUID().toString().take(6)}"
}

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun login(identifier: String, password: String): User {
        val email = resolveEmail(identifier)
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: error("No pudimos iniciar sesión con Firebase")
        val user = buildUserFromFirebase(firebaseUser)
        logUsage("login", user.id)
        return user
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return buildUserFromFirebase(firebaseUser)
    }

    suspend fun requestPasswordReset(identifier: String): Pair<String, String> {
        val email = resolveEmail(identifier)
        auth.sendPasswordResetEmail(email).await()
        val code = generateRecoveryCode()
        val payload = mapOf(
            "email" to email,
            "code" to code,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("password_resets").add(payload).await()
        return email to code
    }

    fun logout() {
        auth.signOut()
    }

    private suspend fun resolveEmail(identifier: String): String {
        val normalized = identifier.trim()
        if (normalized.isBlank()) throw IllegalArgumentException("Ingresa tus credenciales")
        if (Patterns.EMAIL_ADDRESS.matcher(normalized).matches()) {
            return normalized
        }

        val querySnapshot = firestore
            .collection("users")
            .whereEqualTo("username", normalized)
            .limit(1)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.getString("email")
            ?: throw IllegalArgumentException("No encontramos una cuenta llamada $normalized")
    }

    private suspend fun buildUserFromFirebase(firebaseUser: FirebaseUser): User {
        val document = firestore.collection("users").document(firebaseUser.uid).get().await()
        val dto = document.toObject(FirestoreUserDto::class.java)
            ?: FirestoreUserDto(
                id = firebaseUser.uid,
                name = firebaseUser.displayName.orEmpty(),
                username = firebaseUser.email?.substringBefore('@').orEmpty(),
                email = firebaseUser.email.orEmpty()
            )

        val domainUser = dto.copy(
            id = dto.id.ifBlank { firebaseUser.uid },
            email = dto.email.ifBlank { firebaseUser.email.orEmpty() }
        ).toDomain(firebaseUser.email)

        if (!document.exists()) {
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(dto.copy(id = firebaseUser.uid), SetOptions.merge())
                .await()
        }

        return domainUser
    }

    private suspend fun logUsage(event: String, userId: String) {
        firestore.collection("usage_logs")
            .add(
                mapOf(
                    "event" to event,
                    "userId" to userId,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }

    private fun generateRecoveryCode(): String = (100000..999999).random().toString()
}
