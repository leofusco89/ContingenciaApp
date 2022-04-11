package com.example.contingenciaapp.model

import com.example.contingenciaapp.data.Usuario
import com.example.contingenciaapp.ui.LoginActivityView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

interface LoginRepository {
    fun login(firebaseAuth: FirebaseAuth, user: Usuario)
}