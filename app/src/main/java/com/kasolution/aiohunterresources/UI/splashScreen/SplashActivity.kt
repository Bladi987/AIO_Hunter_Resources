package com.kasolution.aiohunterresources.UI.splashScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.kasolution.aiohunterresources.BuildConfig
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Access.AccessActivity
import com.kasolution.aiohunterresources.UI.dashboard.view.Dashboard
import com.kasolution.aiohunterresources.UI.splashScreen.model.settingsData
import com.kasolution.aiohunterresources.UI.splashScreen.viewModel.splashViewModel
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.FadeAnimationUtil
import com.kasolution.aiohunterresources.core.NetworkUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SettingsViewModel: splashViewModel by viewModels()
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    private var versionAPP=""

    private var urlId: urlId?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesAccess = getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = getSharedPreferences("valueUser", Context.MODE_PRIVATE)

        recuperarPreferencias()
        initialSettings()

        NetworkUtils.isInternetAvailable { isConnected ->
            if (isConnected) {
                // La conexión a Internet está disponible
                //solicitar settings del servidor
                SettingsViewModel.onCreate(urlId!!)
//                SettingsViewModel.onCreate(urlId)
            } else {
                DialogUtils.dialogMessage(
                    this,
                    imagen = R.drawable.not_conection,
                    message = "Ups... Al parecer no hay conección a internet, verifique su conexión y vuelva a intentarlo mas tarde.",
                    countOption = 1,
                    onPositiveClick = {
                        //evaluar la posibilidad de volver a intentar la reconexion, por el momento se cerrara
                        finish()
                    })

            }
        }

        SettingsViewModel.versionAPP.observe(this, Observer { version ->
            Log.i("BladiDevSplashActivity", version)
            versionAPP = version
        })
        SettingsViewModel.isloading.observe(this, Observer { loading ->
            if (loading) {
                FadeAnimationUtil.startFadeAnimation(binding.imgLogo)

            } else {
                FadeAnimationUtil.stopFadeAnimation(binding.imgLogo)
                //direccionar()

                saveprefCheckVersionSettings(versionAPP)
            }

        })

        binding.tvVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
    }


    private fun saveprefCheckVersionSettings(versionAPP:String) {
        val editor = preferencesAccess.edit()

        if (BuildConfig.VERSION_NAME != versionAPP) {
            //existe una nueva version
            DialogUtils.dialogMessage(
                this,
                imagen = R.drawable.new_update_lite,
                "Existe una Version nueva, necesita ser actualizada. Solicite a su Administrador dicha actualizacion",
                countOption = 2,
                onPositiveClick = { finish() },
                onNegativeClick = { finish() })
        } else {
            //no hay version nueva
            if (recuperarPreferenciasUser()) {
                val i = Intent(this, Dashboard::class.java)
                startActivity(i)
            } else {
                val i = Intent(this, AccessActivity::class.java)
                startActivity(i)
            }
            finish()
        }

    }

    private fun recuperarPreferencias(){
        val idScript = preferencesAccess.getString("IDSCRIPTACCESS", "")
        val idSheet = preferencesAccess.getString("IDSHEETACCESS", "")
        urlId= urlId(idScript=idScript!!,idSheet=idSheet!!,idFile="",sheetName="")
    }

    private fun initialSettings() {
        if (urlId!!.idScript.isEmpty() || urlId!!.idSheet.isEmpty()) {
            //cargamos valores por defectos
            val editor = preferencesAccess.edit()
            editor.apply{
                putString("IDSCRIPTACCESS", "AKfycbx0HtvtC-_XPVLkNmTGJgB_6xTrFP0gfIKEHsqRR6ZeeTJihAbEEquQKdO2I8DC1BE")
                putString("IDSHEETACCESS", "1tT1q8ltoJNeAnMshA-JMYRgZb7Fkslj4ZxMYcMAsNR4")
            }.apply()
        }
    }
    private fun recuperarPreferenciasUser(): Boolean {
        val id = preferencesUser.getString("ID", "")
        val name = preferencesUser.getString("NAME", "")
        val lastName = preferencesUser.getString("LASTNAME", "")
        val tipo = preferencesUser.getString("TIPO", "")
        return !(id.isNullOrEmpty() || name.isNullOrEmpty() || lastName.isNullOrEmpty() || tipo.isNullOrEmpty())
    }

//    private fun direccionar() {
//        if (recuperarPreferenciasUser()) {
//            var i = Intent(this, Dashboard::class.java)
//            startActivity(i)
//        } else {
//            var i = Intent(this, AccessActivity::class.java)
//            startActivity(i)
//        }
//        finish()
//    }
}