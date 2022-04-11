package com.example.contingenciaapp.ui

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.contingenciaapp.R
import com.example.contingenciaapp.io.DWInterface
import com.example.contingenciaapp.io.DWReceiver
import com.example.contingenciaapp.io.ObservableObject
import com.example.contingenciaapp.model.HomeRepositoryImp
import com.example.contingenciaapp.presenter.HomePresenter
import com.example.contingenciaapp.presenter.HomePresenterImp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), Observer, HomeActivityView, View.OnTouchListener {
    var firebaseAuth: FirebaseAuth? = null
    private lateinit var tvSymbology: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvScanData: TextView
    private lateinit var btnScan: Button
    private lateinit var btnLogout: Button
    private lateinit var presenter: HomePresenter
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private var version65OrOver = false

    companion object {
        const val PROFILE_NAME = "ContingenciaApp"
        const val PROFILE_INTENT_ACTION = "com.example.contingenciaapp.SCAN"
        const val PROFILE_INTENT_START_ACTIVITY = "0"
        const val SCAN_HISTORY_FILE_NAME = "ScanHistory"
        const val SCAN_HISTORY_XML = "ScanHistory.xml"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPresenter()
        setupUI()
    }

    private fun createPresenter() {
        presenter = HomePresenterImp(
            this,
            HomeRepositoryImp()
        )
    }

    private fun setupUI() {
        tvSymbology = findViewById(R.id.tv_symbology)
        tvDateTime  = findViewById(R.id.tv_dateTime)
        tvScanData  = findViewById(R.id.tv_scan_data)
        btnScan     = findViewById(R.id.btn_scan)
        btnLogout   = findViewById(R.id.btn_logout)

        btnScan.setOnTouchListener(this)

        ObservableObject.instance.addObserver(this)

        //  Register broadcast receiver to listen for responses from DW API
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)


        firebaseAuth = FirebaseAuth.getInstance()
        btnLogout.setOnClickListener {
            //On click, sign out by using Firebase and return to Login activity
            firebaseAuth?.signOut()
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //Método creado para launchMode="singleTask", cuando llaman nuevamente a la activity,
        //en lugar de crear una nueva instancia, vuelve a llamar a la que esté en la stack
        //y modifica según lo que diga este método

        //  DataWedge intents received here
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            //  Handle scan intent received from DataWedge, add it to the list of scans
            var scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            var symbology = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_LABEL_TYPE)
            var date = Calendar.getInstance().getTime()
            var df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            var dateTimeString = df.format(date)
            tvSymbology.text = scanData
            tvDateTime.text  = symbology
            tvScanData.text  = dateTimeString
        }
    }

    override fun onResume() {
        super.onResume()

        //  initialized variable is a bit clunky but onResume() is called on each newIntent()
        if (!initialized) {
            //  Create profile to be associated with this application
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }
    override fun update(p0: Observable?, p1: Any?) {
        //  Invoked in response to the DWReceiver broadcast receiver
        var receivedIntent = p1 as Intent
        //  This activity will only receive DataWedge version since that is all we ask for, the
        //  configuration activity is responsible for other return values such as enumerated scanners
        //  If the version is <= 6.5 we reduce the amount of configuration available.  There are
        //  smarter ways to do this, e.g. DW 6.4 introduces profile creation (without profile
        //  configuration) but to keep it simple, we just define a minimum of 6.5 for configuration
        //  functionality
        if (receivedIntent.hasExtra(DWInterface.DATAWEDGE_RETURN_VERSION)) {
            val version = receivedIntent.getBundleExtra(DWInterface.DATAWEDGE_RETURN_VERSION)
            val dataWedgeVersion = version?.getString(DWInterface.DATAWEDGE_RETURN_VERSION_DATAWEDGE)
            if (dataWedgeVersion != null && dataWedgeVersion >= "6.5" && !version65OrOver) {
                version65OrOver = true
                createDataWedgeProfile()
            }
        }
    }

    override fun onTouch(button: View?, event: MotionEvent?): Boolean {
        when (button?.id) {
            R.id.btn_scan -> {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dwInterface.sendCommandString(applicationContext, DWInterface.DATAWEDGE_SEND_SET_SOFT_SCAN,
                            "START_SCANNING")
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        dwInterface.sendCommandString(applicationContext, DWInterface.DATAWEDGE_SEND_SET_SOFT_SCAN,
                            "STOP_SCANNING")
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun createDataWedgeProfile() {
        //  Create and configure the DataWedge profile associated with this application
        //  For readability's sake, I have not defined each of the keys in the DWInterface file
        dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_CREATE_PROFILE, PROFILE_NAME)
        val profileConfig = Bundle()
        profileConfig.putString("PROFILE_NAME", PROFILE_NAME)
        profileConfig.putString("PROFILE_ENABLED", "true") //  These are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE")
        val barcodeConfig = Bundle()
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE")
        barcodeConfig.putString("RESET_CONFIG", "true") //  This is the default but never hurts to specify
        val barcodeProps = Bundle()
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps)
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)
        val appConfig = Bundle()
        appConfig.putString("PACKAGE_NAME", packageName)      //  Associate the profile with this app
        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
        dwInterface.sendCommandBundle(this, DWInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)
        //  You can only configure one plugin at a time in some versions of DW, now do the intent output
        profileConfig.remove("PLUGIN_CONFIG")
        val intentConfig = Bundle()
        intentConfig.putString("PLUGIN_NAME", "INTENT")
        intentConfig.putString("RESET_CONFIG", "true")
        val intentProps = Bundle()
        intentProps.putString("intent_output_enabled", "true")
        intentProps.putString("intent_action", PROFILE_INTENT_ACTION)
        intentProps.putString("intent_delivery", PROFILE_INTENT_START_ACTIVITY)  //  "0"
        intentConfig.putBundle("PARAM_LIST", intentProps)
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
        dwInterface.sendCommandBundle(this, DWInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
