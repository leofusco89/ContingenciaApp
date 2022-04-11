package com.example.contingenciaapp.presenter

import android.util.Log
import com.example.contingenciaapp.R
import com.example.contingenciaapp.data.Usuario
import com.example.contingenciaapp.io.LoginCompleteListener
import com.example.contingenciaapp.model.LoginRepository
import com.example.contingenciaapp.model.LoginRepositoryImp
import com.example.contingenciaapp.ui.LoginActivityView
import com.google.firebase.auth.FirebaseAuth


class LoginPresenterImp(private val view: LoginActivityView) : LoginPresenter,
    LoginCompleteListener {
    var firebaseAuth: FirebaseAuth? = null
    private val model: LoginRepository
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    val TAG = "CustomLog:LoginPresenterImp"

    init {
        Log.i(TAG, "init")
        model = LoginRepositoryImp(this)

        //Ir a Home si el usuario está logueado
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth!!.currentUser
        if (user != null) {
            view.goToHome()
        }

//        For AuthStateListener, its listener will be called when there is a change in the
//        authentication state, will be call when:
//          - Right after the listener has been registered
//          - When a user is signed in
//          - When the current user is signed out
//          - When the current user changes
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //Check if there is a user has already logged in, if so, redirect to Home activity
            val user = firebaseAuth.currentUser
            if (user != null) {
                view.goToHome()
            }
        }
    }

    override fun doLogin(username: String, password: String) {
        Log.i(TAG, "doLogin")
        view.showLoading()
        val user = Usuario(username, password)
        //Verificamos datos obligatorios
        if (checkMandatory(user))
            model.login(firebaseAuth!!, user)
    }

    private fun checkMandatory(user: Usuario): Boolean {
        Log.i(TAG, "checkMandatory")
        var errorFound: Boolean = false

        if (user.username.isEmpty()) {
            view.errorUsername(R.string.empty_username)
            errorFound = true
        } else
            view.errorUsername(null)

        if (user.password.isEmpty()) {
            view.errorPassword(R.string.empty_password)
            errorFound = true
        } else
            view.errorPassword(null)

        if (errorFound) {
            view.hideLoading()
            view.snackbarError(R.string.complete_data)
        }

        return !errorFound
    }

    override fun onSuccess() {
        Log.i(TAG, "onSuccess")
        view.hideLoading()
        val user = firebaseAuth!!.currentUser
        if (user != null)
            view.goToHome()
        else
            view.snackbarError(R.string.error_db_connection)
    }

    override fun onError() {
        Log.i(TAG, "onError")
        view.hideLoading()
        view.snackbarError(R.string.login_error)
    }
}