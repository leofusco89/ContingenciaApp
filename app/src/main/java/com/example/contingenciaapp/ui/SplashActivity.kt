package com.example.contingenciaapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.contingenciaapp.R
import com.example.contingenciaapp.presenter.LoginPresenter
import com.example.contingenciaapp.presenter.LoginPresenterImp
import com.example.contingenciaapp.presenter.SplashPresenter
import com.example.contingenciaapp.presenter.SplashPresenterImp


class SplashActivity : AppCompatActivity(), SplashActivityView {
    private lateinit var presenter: SplashPresenter
    private lateinit var ivLogo: ImageView

    private val TAG = "CustomLog:SplashActivity"
    private val DELAYED_TIME: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                val intent = Intent(this, LoginActivity::class.java)
                ivLogo = findViewById(R.id.iv_logo)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, ivLogo, getString(R.string.transition_logo))

                startActivity(intent, options.toBundle()) //Iniciamos la nueva Activity
                finish() //Finalizamos la Activity actual
            }, DELAYED_TIME ) //Como segundo parámetro le pasamos el tiempo de espera que tendrá antes
        //de que se ejecuté el bloque de código dentro del método run()

        presenter = SplashPresenterImp(this)
    }

    override fun goToLogin() {
        Log.i(TAG, "goToLogin")
        Handler().postDelayed(
            {
                val intent = Intent(this, LoginActivity::class.java)
                ivLogo = findViewById(R.id.iv_logo)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, ivLogo, getString(R.string.transition_logo))

                startActivity(intent, options.toBundle()) //Iniciamos la nueva Activity
                finish() //Finalizamos la Activity actual
            }, DELAYED_TIME ) //Como segundo parámetro le pasamos el tiempo de espera que tendrá antes
        //de que se ejecuté el bloque de código dentro del método run()
    }


    override fun goToHome() {
        Log.i(TAG, "goToHome")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

//loguear,crear arbol para chequeo d si descargo o no, descargar si no descargó, archivo local,
//y subir a clase, etc