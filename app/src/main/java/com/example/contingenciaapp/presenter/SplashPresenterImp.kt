package com.example.contingenciaapp.presenter

import android.util.Log
import com.example.contingenciaapp.io.SplashCompleteListener
import com.example.contingenciaapp.model.LoginRepositoryImp
import com.example.contingenciaapp.model.SplashRepository
import com.example.contingenciaapp.model.SplashRepositoryImp
import com.example.contingenciaapp.ui.SplashActivityView
import com.google.firebase.auth.FirebaseAuth

class SplashPresenterImp(private val view: SplashActivityView) : SplashPresenter,
    SplashCompleteListener {
    var firebaseAuth: FirebaseAuth? = null
    private val model: SplashRepository

    val TAG = "CustomLog:SplashPresenterImp"

    init {
        Log.i(TAG, "init")
        model = SplashRepositoryImp(this)

    }

    override fun getMandatoryData() {
        TODO("Not yet implemented")
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onError() {
        TODO("Not yet implemented")
    }
}