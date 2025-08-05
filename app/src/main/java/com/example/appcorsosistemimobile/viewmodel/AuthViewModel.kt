package com.example.appcorsosistemimobile.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.appcorsosistemimobile.data.model.User

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var currentUserEmail by mutableStateOf<String?>(auth.currentUser?.email)
        private set

    var isLoggedIn by mutableStateOf(auth.currentUser != null)
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                currentUserEmail = auth.currentUser?.email
                isLoggedIn = true
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Errore durante il login")
            }
    }

    fun logout() {
        auth.signOut()
        currentUserEmail = null
        isLoggedIn = false
    }

    fun register(
        name: String,
        surname: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val user = User(
                    email = email,
                    name = name,
                    surname = surname,
                    favouriteDiveSite = emptyList()
                )

                Firebase.firestore
                    .collection("users")
                    .document(email)
                    .set(user)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError("Registrazione avvenuta, ma salvataggio su Firestore fallito") }
            }
            .addOnFailureListener {
                onError(it.message ?: "Errore durante la registrazione")
            }
    }
}
