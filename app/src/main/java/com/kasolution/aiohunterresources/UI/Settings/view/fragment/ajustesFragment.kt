package com.kasolution.aiohunterresources.UI.Settings.view.fragment

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Settings.view.adapter.listFunctionAdapter
import com.kasolution.aiohunterresources.UI.Settings.view.adapter.menuActionAdapter
import com.kasolution.aiohunterresources.UI.Settings.view.model.funcionModel
import com.kasolution.aiohunterresources.UI.Settings.view.model.funcionModelList
import com.kasolution.aiohunterresources.UI.Settings.view.model.userKey
import com.kasolution.aiohunterresources.UI.Settings.viewModel.settingsViewModel
import com.kasolution.aiohunterresources.core.CustomDividerItemDecoration
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.DialogInputDataBinding
import com.kasolution.aiohunterresources.databinding.FragmentAjustesBinding
import com.kasolution.aiohunterresources.databinding.PopupMenuFunctionBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class ajustesFragment : Fragment() {
    private lateinit var binding: FragmentAjustesBinding
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesControlEquipos: SharedPreferences
    private lateinit var preferencesDocumentos: SharedPreferences
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    private val settingsViewModel: settingsViewModel by viewModels()
    private var urlId: urlId? = null
    private lateinit var userName: String
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: menuActionAdapter
    private lateinit var adapterlist: listFunctionAdapter
    private lateinit var funcionList: ArrayList<funcionModel>
    private lateinit var funcionLista: ArrayList<funcionModelList>
    private lateinit var itemSelected: String
    private lateinit var idScriptAccess: String
    private val cajaChica = "Caja Chica"
    private val fichasTecnicas = "Fichas Técnicas"
    private val controlEquipos = "Control de Equipos"
    private val documentos = "Documentos"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAjustesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesControlEquipos =
            requireContext().getSharedPreferences("valueControlEquipos", Context.MODE_PRIVATE)
        preferencesDocumentos =
            requireContext().getSharedPreferences("valueDocumentos", Context.MODE_PRIVATE)
        preferencesAccess =
            requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        idScriptAccess = preferencesAccess.getString("IDSCRIPTACCESS", null).toString()
        funcionList = ArrayList()
        funcionLista = ArrayList()
        recuperarPreferencias()
        initRecyclerListFunction()
        mostrarButtonFuncion()
        binding.btnAddFunction.setOnClickListener() { view ->
            openAddFunction(view)
        }
        binding.llhead.setOnClickListener() {
            preferencesDocumentos.edit().clear().apply()
        }

        settingsViewModel.listKeys.observe(viewLifecycleOwner) { result ->
            result.let { respuesta ->
                if (respuesta.isSuccess) {
                    val lista = respuesta.getOrNull()
                    lista?.let {
                        Log.i("respuesta", it.toString())
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
//                        showMessageError(ex.message.toString())
                        Log.i("respuesta", ex.message.toString())
                    }
                }
            }
        }
    }

    private fun initRecyclerAddFunction(
        binding: PopupMenuFunctionBinding,
        popupWindow: PopupWindow
    ) {
        lmanager = LinearLayoutManager(requireContext())
        adapter = menuActionAdapter(
            listaRecibida = funcionList,
            onClickListener = { function, position ->
                itemSelected = function.name
                openInputCode(true, function.name, position)
                popupWindow.dismiss() // Cierra el popup después de la selección
            }
        )
        binding.rvFunction.layoutManager = lmanager
        binding.rvFunction.adapter = adapter
    }

    private fun initRecyclerListFunction() {
        lmanager = LinearLayoutManager(requireContext())

        adapterlist = listFunctionAdapter(
            listaRecibida = funcionLista,
            onClickListener = { function, position ->
                onItemSelected(function.name)
            }
        )
        val customDivider = CustomDividerItemDecoration(
            context = requireContext(),
            leftMargin = 180, // Espaciado a la izquierda
            rightMargin = 50, // Espaciado a la derecha
            dividerHeight = 1, // Grosor de la línea
            dividerColor = Color.GRAY // Color gris claro
        )
        binding.rvFunction.addItemDecoration(customDivider)
        binding.rvFunction.layoutManager = lmanager
        binding.rvFunction.adapter = adapterlist
    }

    private fun onItemSelected(selected: String) {

        val ajustesDetailsFragment = ajustesDetailsFragment()
        val args = Bundle().apply {
            putString("select", selected)
        }
        ajustesDetailsFragment.arguments = args

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flContenedor, ajustesDetailsFragment)
        fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
        fragmentTransaction.commit()

//        itemSelected = Selected
//        openInputCode(false, Selected, definirPreferences(Selected))
    }

    private fun definirPreferences(funcion: String): SharedPreferences {
        return when (funcion) {
            cajaChica -> preferencesCajaChica
            fichasTecnicas -> preferencesFichasTecnicas
            controlEquipos -> preferencesControlEquipos
            else -> preferencesControlEquipos
        }
    }

    private fun openAddFunction(view: View) {
        // Cargar animación
//        val anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)

        // Inflar el layout del popup
        val inflater = LayoutInflater.from(requireContext())
        val binding = PopupMenuFunctionBinding.inflate(inflater)

        // Crear el PopupWindow
        val popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Iniciar el RecyclerView dentro del popup
        initRecyclerAddFunction(binding, popupWindow)

        // Establecer animación
//        binding.root.startAnimation(anim1)

        // Mostrar el PopupWindow anclado al botón
        popupWindow.showAsDropDown(view, 0, 10)

        // Aplicar transparencia al fondo (opcional)
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_popup
            )
        )
        popupWindow.isOutsideTouchable = true
    }

    private fun openInputCode(nuevo: Boolean, tittle: String, position: Int) {
        // Cargar animación
        val anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)
        // Usar ViewBinding para inflar el layout del diálogo
        val binding = DialogInputDataBinding.inflate(LayoutInflater.from(requireContext()))
        // Análisis y precarga

        // Crear el diálogo y mostrarlo
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        // Establecer animación
        binding.root.animation = anim1
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.lbldialogTittle.text = tittle
        if (tittle == cajaChica) {
            binding.llMonto.visibility = View.VISIBLE
            if (!nuevo) binding.etMontoCajaChica.setText(
                preferencesCajaChica.getString(
                    "MONTOCAJACHICA",
                    null
                )
            )
        }

        // Lógica de los botones
        // Establecer un listener para cuando el diálogo se cierre

        binding.btndialogacept.setOnClickListener {
            val inputMethodManager =
                dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocus = dialog.currentFocus // Obtener el View que tiene el foco
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            }

            val ids = binding.tvInputData.text.toString().split("->")
            when (ids.size) {
                2 -> validarIds(
                    ids[0],
                    ids[1],
                    null,
                    binding.tvInputData,
                    binding.progressBar,
                    binding.imgIcon,
                    binding.imgPaste,
                    dialog,
                    binding.etMontoCajaChica.text.toString(),
                    position
                )

                3 -> validarIds(
                    ids[0],
                    ids[2],
                    ids[1],
                    binding.tvInputData,
                    binding.progressBar,
                    binding.imgIcon,
                    binding.imgPaste,
                    dialog,
                    binding.etMontoCajaChica.text.toString(),
                    position
                )

                else -> binding.tvInputData.setError("Codigo invalido")
            }

        }
        binding.imgPaste.setOnClickListener() {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip

            if (clip != null && clip.itemCount > 0) {
                val item = clip.getItemAt(0)
                binding.tvInputData.setText(item.text) // Establecer el texto del portapapeles en el EditText
            } else {
                Toast.makeText(
                    requireContext(),
                    "No hay texto en el portapapeles",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        dialog.setOnDismissListener {
            //MostrarActionIcon(false)
        }
    }

    private fun mostrarButtonFuncion() {
        if (funcionList.isEmpty()) binding.btnAddFunction.visibility = View.GONE
    }

    private fun recuperarPreferencias() {
        urlId = urlId(
            idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString(),
            "",
            idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString(),
            ""
        )
        userName = preferencesUser.getString("NAME", "") + " " + preferencesUser.getString("LASTNAME", "")
        val monto = preferencesCajaChica.getString("MONTOCAJACHICA", null)
        val urlCc = preferencesCajaChica.getString("URL_SCRIPT", null)
        val idfileCc = preferencesCajaChica.getString("IDFILE", "")
        val idSheetLiquidacionCc = preferencesCajaChica.getString("IDSHEETLIQUIDACION", null)
        Log.i("BladiDev","cajachica: $monto $urlCc $idfileCc $idSheetLiquidacionCc")

        val urlFichas = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", null)
        val idSheetFichas = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", null)
        Log.i("BladiDev","fichas: $urlFichas $idSheetFichas")

        val urlControlEquipos =
            preferencesControlEquipos.getString("URL_SCRIPT_CONTROL_EQUIPOS", null)
        val idSheetControlEquipos =
            preferencesControlEquipos.getString("IDSHEET_CONTROL_EQUIPOS", null)
        Log.i("BladiDev","equipos: $urlControlEquipos $idSheetControlEquipos")

        val urlDocumentos = preferencesDocumentos.getString("URL_SCRIPT_DOCUMENTOS", null)
        val idSheetDocumentos = preferencesDocumentos.getString("IDSHEET_DOCUMENTOS", null)
        Log.i("BladiDev","documentos: $urlDocumentos $idSheetDocumentos")

        if (urlCc != null && idfileCc != null && idSheetLiquidacionCc != null) {
            funcionLista.add(
                funcionModelList(
                    R.drawable.ic_gastos,
                    cajaChica,
                    "Monto asignado  •  Direccion url  •  id file  •  Hoja liquidacion"
                )
            )
        } else {
            funcionList.add(funcionModel(R.drawable.ic_gastos, cajaChica))
        }
        if (urlFichas != null && idSheetFichas != null) {
            funcionLista.add(
                funcionModelList(
                    R.drawable.fichas_tecnicas,
                    fichasTecnicas,
                    "Direccion url  •  Hoja de fichas"
                )
            )
        } else {
            funcionList.add(funcionModel(R.drawable.fichas_tecnicas, fichasTecnicas))
        }
        if (urlControlEquipos != null && idSheetControlEquipos != null) {
            funcionLista.add(
                funcionModelList(
                    R.drawable.ic_equipos,
                    controlEquipos,
                    "Direccion url  •  Hoja de control"
                )
            )
        } else {
            funcionList.add(funcionModel(R.drawable.ic_equipos, controlEquipos))
        }
        if (urlDocumentos != null && idSheetDocumentos != null) {
            funcionLista.add(
                funcionModelList(
                    R.drawable.ic_document,
                    documentos,
                    "Direccion url  •  Hoja de documentos"
                )
            )
        } else {
            funcionList.add(funcionModel(R.drawable.ic_document, documentos))
        }

    }

    private fun savePreferences(
        dialog: AlertDialog,
        namePreferences: SharedPreferences,
        datos: Map<String, String>
    ) {
        val editor = namePreferences.edit()
        for ((key, value) in datos) {
            editor.putString(key, value)
        }
        editor.apply()
        Toast.makeText(requireContext(), "Codigo Ingresado correctamente", Toast.LENGTH_SHORT)
            .show()
        dialog.dismiss()
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

    fun validarIds(
        idScript: String?,
        idSheet: String?,
        idFolder: String?,
        edittext: EditText?,
        progressBar: ProgressBar?,
        icon: ImageView?,
        paste: ImageView?,
        dialog: AlertDialog,
        monto: String?,
        position: Int
    ) {
        Log.i("BladiDev", "validarIds: $idScript $idSheet $idFolder")
        var validacionesCorrectas = 0
        var nroSelection = 0
        val totalValidaciones = listOfNotNull(idScript, idSheet, idFolder).size
        var validacionesPendientes = totalValidaciones
        progressBar?.visibility = View.VISIBLE
        paste?.visibility = View.GONE
        fun asambledSetKey(nroSelection: Int) {
            val cajaChica = if (nroSelection == 1) listOf(monto!!,idScript!!, idFolder!!, idSheet!!) else listOf("","", "", "")
            val fichasTecnicas = if (nroSelection == 2) listOf(idScript!!, idSheet!!) else listOf("", "")
            val controlEquipos = if (nroSelection == 3) listOf(idScript!!, idSheet!!) else listOf("", "")
            val documentos = if (nroSelection == 4) listOf(idScript!!, idSheet!!) else listOf("", "")

            val userkeys = userKey(
                cajaChica = cajaChica,
                fichasTecnicas = fichasTecnicas,
                controlEquipos = controlEquipos,
                documentos = documentos
            )

            settingsViewModel.setKeys(urlId!!, userName, userkeys)
        }

        fun procesarResultados() {
            if (validacionesPendientes == 0) {
                Handler(Looper.getMainLooper()).post {
                    progressBar?.visibility = View.GONE
                    icon?.visibility = View.VISIBLE
                    if (validacionesCorrectas == totalValidaciones) {
                        icon?.setImageResource(R.drawable.ic_check)
                        when (itemSelected) {
                            cajaChica -> {
                                nroSelection = 1
                                savePreferences(
                                    dialog,
                                    preferencesCajaChica,
                                    mapOf(
                                        "MONTOCAJACHICA" to (monto ?: ""),
                                        "URL_SCRIPT" to (idScript ?: ""),
                                        "IDFILE" to (idFolder ?: ""),
                                        "IDSHEETLIQUIDACION" to (idSheet ?: "")
                                    )
                                )
                                funcionLista.add(
                                    funcionModelList(
                                        R.drawable.ic_gastos,
                                        cajaChica,
                                        "Monto asignado  •  Direccion url  •  id file  •  Hoja liquidacion"
                                    )
                                )
                            }

                            fichasTecnicas -> {
                                nroSelection = 2
                                savePreferences(
                                    dialog,
                                    preferencesFichasTecnicas, mapOf(
                                        "URL_SCRIPT_FICHAS" to (idScript ?: ""),
                                        "IDSHEET_FICHAS" to (idSheet ?: "")
                                    )
                                )
                                funcionLista.add(
                                    funcionModelList(
                                        R.drawable.fichas_tecnicas,
                                        fichasTecnicas,
                                        "Direccion url  •  Hoja de fichas"
                                    )
                                )
                            }

                            controlEquipos -> {
                                nroSelection = 3
                                savePreferences(
                                    dialog,
                                    preferencesControlEquipos, mapOf(
                                        "URL_SCRIPT_CONTROL_EQUIPOS" to (idScript ?: ""),
                                        "IDSHEET_CONTROL_EQUIPOS" to (idSheet ?: "")
                                    )
                                )
                                funcionLista.add(
                                    funcionModelList(
                                        R.drawable.ic_equipos,
                                        controlEquipos,
                                        "Direccion url  •  Hoja de control"
                                    )
                                )
                            }

                            else -> {
                                nroSelection = 4
                                savePreferences(
                                    dialog,
                                    preferencesDocumentos, mapOf(
                                        "URL_SCRIPT_DOCUMENTOS" to (idScript ?: ""),
                                        "IDSHEET_DOCUMENTOS" to (idSheet ?: "")
                                    )
                                )
                                funcionLista.add(
                                    funcionModelList(
                                        R.drawable.ic_document,
                                        documentos,
                                        "Direccion url  •  Hoja de documentos"
                                    )
                                )
                            }
                        }
                        dialog.dismiss()
                        adapterlist.notifyItemInserted(funcionLista.size - 1)
                        funcionList.removeAt(position)
                        asambledSetKey(nroSelection)
                        mostrarButtonFuncion()


                    } else {
                        icon?.setImageResource(R.drawable.error_icon)
                        edittext?.setError("Codigo invalido")
                    }
                }
            }
        }


        idScript?.takeIf { it.isNotBlank() }?.let { script ->

            validarIdScript(script) { esValido ->
                if (esValido) validacionesCorrectas++
                validacionesPendientes--
                procesarResultados() // Llamar fuera del if
                Log.i("resultado", "idScript: $esValido")
            }
        }

        idSheet?.takeIf { it.isNotBlank() }?.let { sheet ->
            validarIdSheet(sheet) { esValido ->
                if (esValido) validacionesCorrectas++
                validacionesPendientes--
                procesarResultados() // Llamar fuera del if
                Log.i("resultado", "idSheet: $esValido")
            }
        }

        idFolder?.takeIf { it.isNotBlank() }?.let { folder ->
            validarIdFile(folder) { esValido ->
                if (esValido) validacionesCorrectas++
                validacionesPendientes--
                procesarResultados() // Llamar fuera del if
                Log.i("resultado", "idFolder: $esValido")
            }
        }
    }

}