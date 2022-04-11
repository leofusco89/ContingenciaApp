package com.example.contingenciaapp.model

import android.util.Log
import com.example.contingenciaapp.data.Usuario
import com.example.contingenciaapp.io.LoginCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginRepositoryImp(private val listenerLogin: LoginCompleteListener) : LoginRepository {
    val TAG = "CustomLog:LoginRepositoryImp"

    override fun login(
        firebaseAuth: FirebaseAuth,
        user: Usuario
    ) {
        Log.i(TAG, "login")
        //Agregamos 0 al final de la contraseña si tiene menos de 6 carateres
        var len = user.password.length
        var password = user.password
        while (len<6){
            password = "${password}0"
            len++
        }

        //Intentamos loguear
        firebaseAuth!!.signInWithEmailAndPassword("${user.username}@test.com", password)
            .addOnCompleteListener { task ->
                Log.i(TAG, "addOnCompleteListener")
                if (task.isSuccessful) {
                    listenerLogin.onSuccess()
                } else {
                    listenerLogin.onError()
                }
            }
    }
}