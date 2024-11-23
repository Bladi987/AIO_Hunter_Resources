package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.core.CustomPicasso
import com.kasolution.aiohunterresources.core.FileUtil
import com.kasolution.aiohunterresources.databinding.FragmentAddModelBinding
import com.kasolution.recursoshunter.UI.view.Home.Interfaces.DialogListener
import com.squareup.picasso.Callback
import id.zelory.compressor.Compressor
import id.zelory.compressor.loadBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception

class AddModelFragment : DialogFragment() {
    var listener: DialogListener? = null

    private var _binding: FragmentAddModelBinding? = null
    private val binding get() = _binding!!
    val PICK_IMAGE_REQUEST = 1
    private var actualImage: File? = null
    private var compressedImage: File? = null
    private var Imagen64Base = ""
    private var marcas: Brand? = null
    private var modelos: VehicleModel? = null
    private var modelo: VehicleModel? = null
    var tecnico = ""
    private lateinit var preferencesAccess: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddModelBinding.inflate(layoutInflater, container, false)
        // Carga la animación desde el archivo XML
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)

        // Aplica la animación a la vista del fragmento
        binding.root?.startAnimation(anim)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        arguments?.let {
            if (it.getSerializable("modelo") != null) {
                modelo = it.getSerializable("modelo")!! as VehicleModel
                binding.etModelo.setText(modelo!!.modelo)
                binding.etComentarios.setText(modelo!!.comentarios)
                deCodificarRespuesta(modelo!!.basica)
                binding.etOtros.setText(modelo!!.extra)
                binding.tvtitle.setText("Modificar Registro")
                cargarImagenurl(modelo!!.imagen)
            }
        }
        preferencesAccess = requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        calcularTamano()
        cargarIcon()
        if (modelos != null) cargarModel()
        recuperarPreferencias()
        binding.btnSelectImage.setOnClickListener() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        binding.btnCancelar.setOnClickListener() {
//            listener?.onDataCollected("data Enviado desde el dialog")
            dismiss()

        }
        binding.btnGuardar.setOnClickListener() {
            if (binding.etModelo.text.toString().isEmpty()) {
                binding.etModelo.error = "Campo requerido"
            } else if (Imagen64Base.isEmpty()) {
                binding.btnSelectImage.error="Seleccione una imagen"
                Toast.makeText(context, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
            } else {
                if (modelo == null) {
//                InsertarModelo()
                    val tipoDato = getTypeFromFile(actualImage!!)
                    listener?.onDataCollected(
                        "",
                        Imagen64Base,
                        tipoDato!!,
                        marcas!!.brand,
                        binding.etModelo.text.toString(),
                        binding.etComentarios.text.toString(),
                        codificarRespuestas(),
                        binding.etOtros.text.toString(),
                        tecnico,
                        "",
                        "Revision"
                    )
                } else {
                    var data = "oldData"
                    var tipoDato: String? = ""
                    //modificamos Modelo
                    if (actualImage != null) {
                        //se cambiara la imagen
                        tipoDato = getTypeFromFile(actualImage!!)
                        data = Imagen64Base
                    }

                    listener?.onDataCollectedUpdate(
                        modelo!!.id,
                        data,
                        tipoDato!!,
                        marcas!!.brand,
                        binding.etModelo.text.toString(),
                        modelo!!.imagen,
                        binding.etComentarios.text.toString(),
                        codificarRespuestas(),
                        binding.etOtros.text.toString(),
                        tecnico,
                        "",
                        "Revision"
                    )
                }


                dismiss()
            }
        }
    }

    private fun cargarModel() {
        binding.etModelo.setText(modelos!!.modelo)
        binding.etModelo.isEnabled = false
    }

    private fun cargarIcon() {
        val customPicasso = CustomPicasso.getInstance(requireContext())
        customPicasso.load("https://drive.google.com/uc?export=view&id=${marcas!!.icon}").into(
            binding.imgIcon,
            object : Callback {
                override fun onSuccess() {
                    //binding.imgIcon.animate().alpha(1f).setDuration(300)
                    binding.imgIcon.visibility = View.VISIBLE
                    binding.imgIcon.animate().alpha(1f).setDuration(300)
                }

                override fun onError(e: Exception?) {
                }
            })
    }

    private fun cargarImagenurl(link: String) {
        Log.i("BladiDev", "cargarImagenurl: $link")
        val customPicasso = CustomPicasso.getInstance(requireContext())
        customPicasso.load("https://drive.google.com/uc?export=view&id=${link}").into(
            binding.ivimagen,
            object : Callback {
                override fun onSuccess() {
                    //binding.imgIcon.animate().alpha(1f).setDuration(300)
                    Imagen64Base = "recoveryImg"
                    binding.ivimagen.visibility = View.VISIBLE
                    binding.ivimagen.animate().alpha(1f).duration = 300
                }

                override fun onError(e: Exception?) {
                }
            })
    }

    fun codificarRespuestas(): String {
        var positivo = "0"
        var gnd = "0"
        var ignition = "0"
        var corte = "0"
        var pestillos = "0"
        if (binding.cbPositivo.isChecked) positivo = "1"
        if (binding.cbGND.isChecked) gnd = "1"
        if (binding.cbIgnition.isChecked) ignition = "1"
        if (binding.cbCorte.isChecked) corte = "1"
        if (binding.cbPestillos.isChecked) pestillos = "1"
        return "$positivo,$gnd,$ignition,$corte,$pestillos"
    }

    fun deCodificarRespuesta(codigo: String) {
        var array = codigo.split(",")
        if (array[0] == "1") binding.cbPositivo.isChecked = true
        if (array[1] == "1") binding.cbGND.isChecked = true
        if (array[2] == "1") binding.cbIgnition.isChecked = true
        if (array[3] == "1") binding.cbCorte.isChecked = true
        if (array[4] == "1") binding.cbPestillos.isChecked = true
    }

    fun getlistamarcas(marca: Brand) {
        marcas = marca
    }

    fun getListModel(modelo: VehicleModel) {
        modelos = modelo
    }

    fun calcularTamano() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenheigth = displayMetrics.heightPixels
        // Calcular el ancho máximo como el 90% del ancho de la pantalla
        val maxWidth = (screenWidth * 0.9).toInt()
        val maxheight = (screenheigth * 0.9).toInt()
        dialog?.window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!")
                return
            }
            try {
                actualImage = data.data?.let {
                    FileUtil.from(requireContext(), it)?.also {
                        binding.ivimagen.visibility = View.VISIBLE
                        binding.ivimagen.setImageBitmap(loadBitmap(it))
                        //binding.actualSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
//                        clearImage()
                    }

                }
                compressImage()
            } catch (e: IOException) {
                showError("Failed to read picture data!")
                e.printStackTrace()
            }
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun compressImage() {
        actualImage?.let { imageFile ->
            lifecycleScope.launch {
                // Default compression
                compressedImage = Compressor.compress(requireContext(), imageFile)
//                setCompressedImage()
                Imagen64Base = encodeImageToBase64(compressedImage!!)

            }
        } ?: showError("Please choose an image!")
    }

    fun getTypeFromFile(file: File): String? {
        val extension = file.extension.toLowerCase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    fun encodeImageToBase64(file: File): String {
        val byteArray = FileInputStream(file).readBytes()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun recuperarPreferencias() {
        val nameUser=preferencesAccess.getString("NAME", "")
        val lastName=preferencesAccess.getString("LASTNAME", "")
        tecnico ="$nameUser $lastName"
    }
}