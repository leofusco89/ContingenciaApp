package com.example.contingenciaapp.presenter

import android.view.MotionEvent
import android.view.View
import com.example.contingenciaapp.R
import com.example.contingenciaapp.io.DWInterface
import com.example.contingenciaapp.model.HomeRepository
import com.example.contingenciaapp.ui.HomeActivityView

class HomePresenterImp (
    private val view: HomeActivityView,
    private val model: HomeRepository
) : HomePresenter {

}