package com.example.contingenciaapp.ui

interface LoginActivityView {
    fun showLoading()
    fun hideLoading()
    fun snackbarError(messageId: Int)
    fun errorUsername(messageId: Int?)
    fun errorPassword(messageId: Int?)
    fun goToHome()
}