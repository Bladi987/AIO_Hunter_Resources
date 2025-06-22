package com.kasolution.aiohunterresources.UI.splashScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kasolution.aiohunterresources.BuildConfig
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Access.AccessActivity
import com.kasolution.aiohunterresources.UI.dashboard.view.Dashboard
import com.kasolution.aiohunterresources.UI.splashScreen.fragment.newVersionFragment
import com.kasolution.aiohunterresources.UI.splashScreen.viewModel.SplashViewModel
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.FadeAnimationUtil
import com.kasolution.aiohunterresources.core.NetworkUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.ActivitySplashBinding
import java.io.File

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashViewModel: SplashViewModel
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    private var versionAPP:String?=null
    private var linkUpdate:String?=null

    private var urlId: urlId? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clearFileUpdate()
        // Obtener el ViewModel usando ViewModelProvider con el contexto de la aplicación
        splashViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SplashViewModel::class.java)
        preferencesAccess = getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = getSharedPreferences("valueUser", Context.MODE_PRIVATE)

        recuperarPreferencias()
//        initialSettings()

        NetworkUtils.isInternetAvailable { isConnected ->
            if (isConnected) {
                // La conexión a Internet está disponible
                //solicitar settings del servidor
                splashViewModel.onCreate(urlId!!)
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

        splashViewModel.dataSettings.observe(this, Observer { result ->
            Log.d("SplashActivity", "versionAPP: $result")
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { data ->
                        versionAPP = data.first
                        linkUpdate = data.second
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        splashViewModel.isloading.observe(this, Observer { loading ->
            if (loading) {
                FadeAnimationUtil.startFadeAnimation(binding.imgLogo)
            } else {
                FadeAnimationUtil.stopFadeAnimation(binding.imgLogo)
                if (versionAPP != null)
                saveprefCheckVersionSettings()
                else
                    showMessageError("101") //no se encontro version
            }

        })

        binding.tvVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
    }


    private fun saveprefCheckVersionSettings() {
        if (!versionAPP.isNullOrEmpty()) {
            if (BuildConfig.VERSION_NAME != versionAPP) {
                //existe una nueva version
                if (!linkUpdate.isNullOrEmpty()){
                    val dialogFragment = newVersionFragment()
                    dialogFragment.isCancelable = false
                    val args = Bundle().apply {
                        putString("message", "Existe una Version nueva, necesita ser actualizada.")
                        putString("linkUpdate", linkUpdate)
                    }
                    dialogFragment.arguments = args
                    dialogFragment.show(supportFragmentManager, "newVersionFragment")
                }else showMessageError("102") //no se encontro link de descarga


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
    }

    private fun recuperarPreferencias() {
        val newIdScript = resources.getString(R.string.idScript)
        val newIdSheet = resources.getString(R.string.idSheet)
        val idScript = preferencesAccess.getString("IDSCRIPTACCESS", null)
        val idSheet = preferencesAccess.getString("IDSHEETACCESS", null)

        if ((idScript == null && idSheet == null) || (idScript != newIdScript || idSheet != newIdSheet)) {
            preferencesAccess.edit().apply {
                putString("IDSCRIPTACCESS", newIdScript)
                putString("IDSHEETACCESS", newIdSheet)
            }.apply()
            urlId = urlId(idScript = newIdScript, idSheet = newIdSheet, idFile = "", sheetName = "")
        } else {
            urlId = urlId(idScript = idScript, idSheet = idSheet, idFile = "", sheetName = "")
        }
    }


    private fun recuperarPreferenciasUser(): Boolean {
        val name = preferencesUser.getString("NAME", null)
        val lastName = preferencesUser.getString("LASTNAME", null)
        val tipo = preferencesUser.getString("TIPO", null)
        return !(name.isNullOrEmpty() || lastName.isNullOrEmpty() || tipo.isNullOrEmpty())
    }

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            this,
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
            onPositiveClick = {
                finish()
            }
        )
    }

    private fun clearFileUpdate() {
        val file = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
        if (file.exists()) {
            val eliminado = file.delete()
            if (eliminado) {
                Log.i("APK", "Archivo eliminado correctamente.")
            } else {
                Log.e("APK", "No se pudo eliminar el archivo.")
            }
        } else {
            Log.w("APK", "El archivo no existe.")
        }
    }
}