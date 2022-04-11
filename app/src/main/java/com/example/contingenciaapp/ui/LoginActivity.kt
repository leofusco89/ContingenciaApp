package com.example.contingenciaapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.contingenciaapp.R
import com.example.contingenciaapp.presenter.LoginPresenter
import com.example.contingenciaapp.presenter.LoginPresenterImp
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity(), LoginActivityView {
    private lateinit var presenter: LoginPresenter
    private lateinit var clMain: ConstraintLayout
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var llProgressBar: LinearLayout

    private val TAG = "CustomLog:LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenterImp(this)
        setupUI()
    }

    private fun setupUI() {
        Log.i(TAG, "setupUI")
        clMain = findViewById(R.id.cl_main)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        llProgressBar = findViewById(R.id.ll_progress_bar)

        btnLogin.setOnClickListener {
            Log.i(TAG, "setOnClickListener:btnLogin")
            presenter.doLogin(
                etUsername.text.toString(),
                etPassword.text.toString()
            )
        }
    }

    override fun showLoading() {
        Log.i(TAG, "showLoading")
        llProgressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Log.i(TAG, "hideLoading")
        llProgressBar.visibility = View.GONE
    }

    override fun snackbarError(messageId: Int) {
        Log.i(TAG, "snackbarError")
        Snackbar.make(clMain, this.getString(messageId), Snackbar.LENGTH_LONG).show()
    }

    override fun errorUsername(messageId: Int?) {
        Log.i(TAG, "errorUsername")
        if (messageId != null)
            etUsername.error = this.getString(messageId)
        else
            etUsername.error = null
    }

    override fun errorPassword(messageId: Int?) {
        Log.i(TAG, "errorPassword")
        if (messageId != null)
            etPassword.error = this.getString(messageId)
        else
            etPassword.error = null
    }

    override fun goToHome() {
        Log.i(TAG, "goToHome")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}