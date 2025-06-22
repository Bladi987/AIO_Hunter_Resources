package com.kasolution.aiohunterresources.UI.splashScreen.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.splashScreen.SplashActivity
import com.kasolution.aiohunterresources.UI.splashScreen.viewModel.SplashViewModel
import com.kasolution.aiohunterresources.UI.utilities.PermissionManager
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.databinding.FragmentNewVersionBinding
import java.io.File


class newVersionFragment : DialogFragment() {
    private lateinit var binding: FragmentNewVersionBinding
    private lateinit var splashViewModel: SplashViewModel
    private var mensaje: String? = null
    private lateinit var permissionManager: PermissionManager
    private var apkPath: String? = null
    private var linkUpdate: String? = null
    private var shouldCheckInstallPermission = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewVersionBinding.inflate(layoutInflater, container, false)
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        binding.root.startAnimation(anim)
        val view = binding.root
        return view
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("apkPath", apkPath)
        outState.putBoolean("shouldCheckInstallPermission", shouldCheckInstallPermission)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calcularTamano()
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        arguments?.let {
            mensaje = it.getString("message")
            linkUpdate=it.getString("linkUpdate")
        }
        // Restaurar estado previo
        apkPath = savedInstanceState?.getString("apkPath")
        shouldCheckInstallPermission = savedInstanceState?.getBoolean("shouldCheckInstallPermission") ?: false

        permissionManager = PermissionManager(requireActivity())

        // Obtener el ViewModel usando el contexto de la actividad
        splashViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SplashViewModel::class.java)

        val lottieAnimationView: LottieAnimationView = binding.ivimagen
        lottieAnimationView.setAnimation(R.raw.new_version)
        lottieAnimationView.playAnimation()
        binding.tvTexto.text = mensaje

        splashViewModel.downloadProgress.observe(this) { progress ->
            binding.progressBar.progress = progress
            binding.progressTextView.text = "$progress%"

            binding.progressBar.post {
                val barWidth = binding.progressBar.width
                val labelWidth = binding.progressTextView.width
                val ratio = progress / 100f
                val margin = 8 * resources.displayMetrics.density
                val newX = ((barWidth - labelWidth) * ratio) - margin
                binding.progressTextView.translationX = newX.coerceAtLeast(0f)
            }
        }

        splashViewModel.downloadStatusText.observe(this) { status ->
            binding.tvbyte.text = status // ejemplo: "Descargando (245 KB / 1024 KB)"
        }

        splashViewModel.downloadStatus.observe(this) { isSuccess ->
            if (!isSuccess) {
                showMessageError("103")//no se encontro archivo
                binding.llnotification.visibility = View.VISIBLE
                binding.flcontnedorExit.visibility = View.VISIBLE
                binding.llDownload.visibility = View.GONE
                binding.tvTime.text=""
                binding.tvbyte.text=""
                splashViewModel.reiniciarProgreso()
            }else{  //descargado con exito
                binding.llnotification.visibility = View.VISIBLE
                binding.flcontnedorExit.visibility = View.VISIBLE
                binding.llDownload.visibility = View.GONE
                binding.tvTexto.text="Actualización descargada con exito"
                binding.btnAceptar.text="Instalar"
            }
        }

        splashViewModel.filePath.observe(this) { path ->
            apkPath = path
            if (!path.isNullOrEmpty()) checkInstallPermissionAndInstall()
        }

        splashViewModel.remainingTime.observe(this) { time ->
            binding.tvTime.text = "Tiempo restante: ${time / 1000}s"
        }


        binding.btnAceptar.setOnClickListener() {
            if (binding.btnAceptar.text == "Instalar") {
                checkInstallPermissionAndInstall()
            } else if(!linkUpdate.isNullOrEmpty()){
                binding.llnotification.visibility = View.GONE
                binding.flcontnedorExit.visibility = View.GONE
                binding.llDownload.visibility = View.VISIBLE

                splashViewModel.downloadFile(linkUpdate!!)
            }else showMessageError("102")
        }
        binding.ivExit.setOnClickListener() {
            (requireActivity() as? SplashActivity)?.finish()
        }
    }

    private fun calcularTamano() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenheigth = displayMetrics.heightPixels
        // Calcular el ancho máximo como el 90% del ancho de la pantalla
        val maxWidth = (screenWidth * 0.9).toInt()
        val maxheight = (screenheigth * 0.9).toInt()
        dialog?.window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun checkInstallPermissionAndInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!requireContext().packageManager.canRequestPackageInstalls()) {
                shouldCheckInstallPermission = true
                permissionManager.solicitarPermisoInstalar()
                return
            }
        }
        apkPath?.let { instalarApk(it) }
    }

    private fun instalarApk(path: String) {
        val file = File(path)
        val packageName = requireContext().packageName
        val uri = FileProvider.getUriForFile(requireContext(), "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("Install Error", "Error al iniciar la instalación: ${e.message}", e)
            Toast.makeText(requireContext(), "Error al iniciar la instalación", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun mostrarDialogoPermisoDenegado() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permiso requerido")
            .setMessage("Para instalar la actualización, necesitas permitir instalaciones desde fuentes desconocidas.")
            .setPositiveButton("Abrir ajustes") { _, _ ->
                permissionManager.solicitarPermisoInstalar()
                shouldCheckInstallPermission = true
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(requireContext(), "Instalación cancelada por el usuario", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (shouldCheckInstallPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (permissionManager.verificarPermisoInstalar()) {
                shouldCheckInstallPermission = false
                apkPath?.let { instalarApk(it) }
            } else {
                shouldCheckInstallPermission = false
                mostrarDialogoPermisoDenegado()
            }
        }
    }
    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            requireContext(),
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
            onPositiveClick = {
                //dismiss()
            }
        )
    }

}