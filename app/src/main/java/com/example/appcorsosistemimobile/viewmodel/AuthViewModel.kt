package com.example.appcorsosistemimobile.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.appcorsosistemimobile.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _currentUserEmail = MutableStateFlow<String?>(auth.currentUser?.email)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun loadUserProfile(email: String) {
        Firebase.firestore
            .collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _currentUser.value = document.toObject(User::class.java)
                }
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userEmail = auth.currentUser?.email
                _currentUserEmail.value = userEmail
                _isLoggedIn.value = true
                userEmail?.let { loadUserProfile(it) }
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Errore durante il login")
            }
    }

    fun logout() {
        auth.signOut()
        _currentUserEmail.value = null
        _isLoggedIn.value = false
        _currentUser.value = null
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

    fun initSession() {
        val user = auth.currentUser
        if (user != null) {
            _currentUserEmail.value = user.email
            _isLoggedIn.value = true
            user.email?.let { loadUserProfile(it) }
        }
    }

}
