package com.kasolution.aiohunterresources.UI.Settings.view.fragment

import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Settings.view.model.userKey
import com.kasolution.aiohunterresources.UI.Settings.viewModel.settingsViewModel
import com.kasolution.aiohunterresources.core.CurrencyTextWatcher
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentAjustesDetailsBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class ajustesDetailsFragment : Fragment() {
    private lateinit var binding: FragmentAjustesDetailsBinding
    private val settingsViewModel: settingsViewModel by viewModels()
    private lateinit var selected: String
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesControlEquipos: SharedPreferences
    private lateinit var preferencesDocumentos: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences

    private var urlId: urlId? = null
    private lateinit var userName: String
    private lateinit var idScriptAccess: String
    private lateinit var idSheetAccess: String
    private val cajaChica = "Caja Chica"
    private val fichasTecnicas = "Fichas Técnicas"
    private val controlEquipos = "Control de Equipos"

    private var monto: String? = null
    private var idScript: String? = null
    private var idSheetFolder: String? = null
    private var saveItem: ArrayList<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAjustesDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            selected = it.getString("select")!!
            recuperarPreferencias(selected)
        }
        binding.tvTittle.text = selected

        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnSave.setOnClickListener() {
            if (binding.llMonto.visibility == View.VISIBLE) {
                if (binding.etMontoCajaChica.text.toString() == "S/ 0.00") {
                    binding.etMontoCajaChica.error = "Ingrese un monto válido"
                    return@setOnClickListener
                }
            }
            saveItem = arrayListOf()
            val montoLimpioEditText =
                binding.etMontoCajaChica.text.toString().replace("S/ ", "").trim()
            if (binding.etMontoCajaChica.text.toString()
                    .isNotEmpty() && montoLimpioEditText != monto
            ) {
                Log.i("BladiDev", "Monto: $montoLimpioEditText")
                Log.i("BladiDev", "Monto: $monto")
                saveItem?.add("Monto caja Chica")
                val montoLimpio = binding.etMontoCajaChica.text.toString().replace("S/ ", "").trim()
                preferencesCajaChica.edit()
                    .putString("MONTOCAJACHICA", montoLimpio).apply()
                monto = montoLimpio
            }
            validarIds(
                idScript = if (binding.etInputUrl.text.toString() != idScript) binding.etInputUrl.text.toString() else null,
                idSheetFolder = if (binding.etInputIdSheetIdFile.text.toString() != idSheetFolder) binding.etInputIdSheetIdFile.text.toString() else null,
                progressBarScript = if (binding.etInputUrl.text.toString() != idScript) binding.progressScript else null,
                progressBarSheetFolder = if (binding.etInputIdSheetIdFile.text.toString() != idSheetFolder) binding.progressSheet else null,
                iconScript = if (binding.etInputUrl.text.toString() != idScript) binding.iconScript else null,
                iconSheetFolder = if (binding.etInputIdSheetIdFile.text.toString() != idSheetFolder) binding.iconSheet else null,
                pasteScript = if (binding.etInputUrl.text.toString() != idScript) binding.ivPasteScript else null,
                pasteSheetFolder = if (binding.etInputIdSheetIdFile.text.toString() != idSheetFolder) binding.ivPasteSheet else null,
                isfolder = binding.etMontoCajaChica.visibility == View.VISIBLE
            )
        }
        binding.ivPasteScript.setOnClickListener() {
            pasteCode(binding.etInputUrl)
        }
        binding.ivPasteSheet.setOnClickListener() {
            pasteCode(binding.etInputIdSheetIdFile)
        }
        binding.etMontoCajaChica.addTextChangedListener(CurrencyTextWatcher(binding.etMontoCajaChica))
    }

    private fun recuperarPreferencias(selected: String) {

        when (selected) {
            cajaChica -> {
                preferencesCajaChica =
                    requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
                monto = preferencesCajaChica.getString("MONTOCAJACHICA", "0") ?: "0"
                idScript = preferencesCajaChica.getString("URL_SCRIPT", null)
                idSheetFolder = preferencesCajaChica.getString("IDFILE", "")

                //formateamos el monto recuperado
                val montoFormateado =
                    String.format(Locale.getDefault(), "S/ %.2f", formatearMonto(monto!!).toFloat())
                        .trim()
                //asignamos los valores recuperados
                //asignamos los valores recuperados y mostramos controles necesarios
                binding.llMonto.visibility = View.VISIBLE
                binding.tvInputNameSheet.text = "ID Folder Caja Chica"
                binding.etMontoCajaChica.setText(montoFormateado)
                binding.etInputUrl.setText(idScript)
                binding.etInputIdSheetIdFile.setText(idSheetFolder)
            }

            fichasTecnicas -> {
                preferencesFichasTecnicas = requireContext().getSharedPreferences(
                    "valueFichasTecnicas",
                    Context.MODE_PRIVATE
                )
                idScript = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", null)
                idSheetFolder = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", null)
                //asignamos los valores recuperados y mostramos controles necesarios
                binding.etInputUrl.setText(idScript)
                binding.etInputIdSheetIdFile.setText(idSheetFolder)
            }

            controlEquipos -> {
                preferencesControlEquipos = requireContext().getSharedPreferences(
                    "valueControlEquipos",
                    Context.MODE_PRIVATE
                )
                idScript = preferencesControlEquipos.getString("URL_SCRIPT_CONTROL_EQUIPOS", null)
                idSheetFolder = preferencesControlEquipos.getString("IDSHEET_CONTROL_EQUIPOS", null)
                //asignamos los valores recuperados y mostramos controles necesarios
                binding.etInputUrl.setText(idScript)
                binding.etInputIdSheetIdFile.setText(idSheetFolder)
            }

            else -> {
                preferencesDocumentos =
                    requireContext().getSharedPreferences("valueDocumentos", Context.MODE_PRIVATE)
                idScript = preferencesDocumentos.getString("URL_SCRIPT_DOCUMENTOS", null)
                idSheetFolder = preferencesDocumentos.getString("IDSHEET_DOCUMENTOS", null)
                //asignamos los valores recuperados y mostramos controles necesarios
                binding.etInputUrl.setText(idScript)
                binding.etInputIdSheetIdFile.setText(idSheetFolder)
            }
        }
        preferencesAccess =
            requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        idScriptAccess = preferencesAccess.getString("IDSCRIPTACCESS", null).toString()
        idSheetAccess = preferencesAccess.getString("IDSHEETACCESS", null).toString()

        urlId = urlId(
            idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString(),
            "",
            idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString(),
            ""
        )
        userName =
            preferencesUser.getString("NAME", "") + " " + preferencesUser.getString("LASTNAME", "")

    }

    fun validarIdScript(idScript: String, callback: (Boolean) -> Unit) {
        val url = "https://script.google.com/macros/s/$idScript/exec"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString())
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val esValido = response.isSuccessful
                if (!esValido)
                    Log.e("Error", "El ID del Apps Script no es valido.")

                callback(esValido)
            }
        })
    }

    fun validarIdFile(idFile: String, callback: (Boolean) -> Unit) {
        val url = "https://drive.google.com/drive/folders/$idFile"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString())
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val esValido = response.isSuccessful
                if (!esValido) Log.e("Error", "El ID del File no es valido.")
                callback(esValido)
            }
        })
    }

    fun validarIdSheet(idSheet: String, callback: (Boolean) -> Unit) {
        val url = "https://script.google.com/macros/s/$idScriptAccess/exec?idSheet=$idSheet"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString())
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    Log.e("Error", "Respuesta vacía o nula")
                    callback(false)
                    return
                }

                try {
                    val json = JSONObject(responseBody)
                    val esValido = json.optString("status") == "valid"
                    if (!esValido) Log.e("Error", "El ID del Sheet no es válido.")
                    callback(esValido)
                } catch (e: Exception) {
                    Log.e("Error", "No se pudo parsear JSON: ${e.message}")
                    callback(false) // Si no es JSON, asumimos que no es válido
                }
            }
        })
    }

    fun formatearMonto(monto: String): Double {
        return monto.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
    }

    //nueva implementacion
    fun validarIds(
        idScript: String?,
        idSheetFolder: String?,
        progressBarScript: ProgressBar?,
        progressBarSheetFolder: ProgressBar?,
        iconScript: ImageView?,
        iconSheetFolder: ImageView?,
        pasteScript: ImageView?,
        pasteSheetFolder: ImageView?,
        isfolder: Boolean = false
    ) {
        var validacionesCompletadas = 0
        val totalValidaciones = listOfNotNull(idScript, idSheetFolder).size
        fun savePreferences(typeId: String) {
            when (selected) {
                cajaChica -> {
                    when (typeId) {
                        "script" -> preferencesCajaChica.edit()
                            .putString("URL_SCRIPT", binding.etInputUrl.text.toString()).apply()

                        "sheet" -> preferencesCajaChica.edit()
                            .putString("IDSHEETLIQUIDACION", binding.etInputIdSheetIdFile.text.toString())
                            .apply()

                        "file" -> preferencesCajaChica.edit()
                            .putString("IDFILE", binding.etInputIdSheetIdFile.text.toString()).apply()
                    }
                    saveItem?.add("Caja chica -> $typeId")
                    //enviamos los keys al servidor
                    val userkeys = userKey(
                        cajaChica = listOf(
                            binding.etMontoCajaChica.text.toString(),
                            binding.etInputUrl.text.toString(),
                            binding.etInputIdSheetIdFile.text.toString()
                        ),
                        fichasTecnicas = listOf("", ""),
                        controlEquipos = listOf("", ""),
                        documentos = listOf("", "")
                    )
                    settingsViewModel.setKeys(urlId!!, userName, userkeys)
                }

                fichasTecnicas -> {
                    when (typeId) {
                        "script" -> preferencesFichasTecnicas.edit()
                            .putString("URL_SCRIPT_FICHAS", binding.etInputUrl.text.toString())
                            .apply()

                        "sheet" -> preferencesFichasTecnicas.edit()
                            .putString("IDSHEET_FICHAS", binding.etInputIdSheetIdFile.text.toString())
                            .apply()
                    }
                    saveItem?.add("Fichas técnicas -> $typeId")
                    //enviamos los keys al servidor
                    val userkeys = userKey(
                        cajaChica = listOf("", "", "", ""),
                        fichasTecnicas = listOf(
                            binding.etInputUrl.text.toString(),
                            binding.etInputIdSheetIdFile.text.toString()
                        ),
                        controlEquipos = listOf("", ""),
                        documentos = listOf("", "")
                    )
                    settingsViewModel.setKeys(urlId!!, userName, userkeys)
                }

                controlEquipos -> {
                    when (typeId) {
                        "script" -> preferencesControlEquipos.edit().putString(
                            "URL_SCRIPT_CONTROL_EQUIPOS",
                            binding.etInputUrl.text.toString()
                        ).apply()

                        "sheet" -> preferencesControlEquipos.edit().putString(
                            "IDSHEET_CONTROL_EQUIPOS",
                            binding.etInputIdSheetIdFile.text.toString()
                        ).apply()
                    }
                    saveItem?.add("Control equipos -> $typeId")
                    val userkeys = userKey(
                        cajaChica = listOf("", "", "", ""),
                        fichasTecnicas = listOf("", ""),
                        controlEquipos = listOf(
                            binding.etInputUrl.text.toString(),
                            binding.etInputIdSheetIdFile.text.toString()
                        ),
                        documentos = listOf("", "")
                    )
                    settingsViewModel.setKeys(urlId!!, userName, userkeys)
                }

                else -> {
                    when (typeId) {
                        "script" -> preferencesDocumentos.edit()
                            .putString("URL_SCRIPT_DOCUMENTOS", binding.etInputUrl.text.toString())
                            .apply()

                        "sheet" -> preferencesDocumentos.edit()
                            .putString("IDSHEET_DOCUMENTOS", binding.etInputIdSheetIdFile.text.toString())
                            .apply()
                    }
                    saveItem?.add("Documentos -> $typeId")
                    val userkeys = userKey(
                        cajaChica = listOf("", "", "", ""),
                        fichasTecnicas = listOf("", ""),
                        controlEquipos = listOf("", ""),
                        documentos = listOf(
                            binding.etInputUrl.text.toString(),
                            binding.etInputIdSheetIdFile.text.toString()
                        )
                    )
                    settingsViewModel.setKeys(urlId!!, userName, userkeys)
                }
            }
        }

        fun verificarFinalizacion() {
            validacionesCompletadas++

            if (validacionesCompletadas == totalValidaciones || totalValidaciones == 0) {
                Log.d("DEBUG_VALIDACION", "Elementos en saveItem antes del Toast: $saveItem")

                if (saveItem!!.isNotEmpty()) {
                    Log.i("BladiDev", "saveItem antes del Toast: $saveItem")
                    if (saveItem!!.size == 1 && saveItem!!.contains("Monto caja Chica")) {
                        val userkeys = userKey(
                            cajaChica = listOf(
                                binding.etMontoCajaChica.text.toString().replace("S/ ", "").trim(),
                                binding.etInputUrl.text.toString(),
                                binding.etInputIdSheetIdFile.text.toString()
                            ),
                            fichasTecnicas = listOf("", ""),
                            controlEquipos = listOf("", ""),
                            documentos = listOf("", "")
                        )
                        settingsViewModel.setKeys(urlId!!, userName, userkeys)
                    }
                    mostrarToast(saveItem!!) // Solo usamos `saveItem`
                    Log.d("DEBUG_VALIDACION", "saveItem después del Toast: $saveItem")
                }
            }
        }

        fun actualizarUI(
            progressBar: ProgressBar?, icono: ImageView?, esValido: Boolean, typeId: String
        ) {
            Handler(Looper.getMainLooper()).post {
                progressBar?.visibility = View.GONE  // Ocultar ProgressBar
                icono?.visibility = View.VISIBLE    // Mostrar icono
                if (esValido) {
                    icono?.setImageResource(R.drawable.ic_check)
                    savePreferences(typeId)
                } else icono?.setImageResource(R.drawable.error_icon)
                verificarFinalizacion()
            }
        }

        fun iniciarValidacion(progressBar: ProgressBar?, icono: ImageView?, paste: ImageView?) {
            Handler(Looper.getMainLooper()).post {
                progressBar?.visibility = View.VISIBLE  // Mostrar ProgressBar
                icono?.visibility = View.GONE          // Ocultar icono
                paste?.visibility = View.GONE
            }
        }
        // Si no hay IDs para validar, solo llamamos a `verificarFinalizacion()`
        if (totalValidaciones == 0) {
            verificarFinalizacion()
            return
        }

        idScript?.takeIf { it.isNotBlank() }?.let { script ->
            iniciarValidacion(progressBarScript, iconScript, pasteScript)
            validarIdScript(script) { esValido ->
                actualizarUI(progressBarScript, iconScript, esValido, "script")
            }
        }
        idSheetFolder?.takeIf { it.isNotBlank() }?.let { id ->
            iniciarValidacion(progressBarSheetFolder, iconSheetFolder, pasteSheetFolder)
            if (isfolder) {
                validarIdFile(id) { esValido ->
                    actualizarUI(progressBarSheetFolder, iconSheetFolder, esValido, "folder")
                }
            } else {
                validarIdSheet(id) { esValido ->
                    actualizarUI(progressBarSheetFolder, iconSheetFolder, esValido, "sheet")
                }
            }
        }

    }

    fun mostrarToast(idsGuardados: List<String>) {
        val mensaje =
            if (idsGuardados.isNotEmpty()) "Se guardaron con éxito:\n• ${idsGuardados.joinToString("\n• ")}" else "No se pudo guardar ningún ID."
        DialogUtils.dialogMessageResponse(requireContext(), mensaje)
        saveItem!!.clear() // Limpiamos después de mostrar el mensaje
    }

    private fun pasteCode(edittext: EditText) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip

        if (clip != null && clip.itemCount > 0) {
            val item = clip.getItemAt(0)
            edittext.setText(item.text) // Establecer el texto del portapapeles en el EditText
        } else {
            Toast.makeText(requireContext(), "No hay texto en el portapapeles", Toast.LENGTH_SHORT)
                .show()
        }
    }
}