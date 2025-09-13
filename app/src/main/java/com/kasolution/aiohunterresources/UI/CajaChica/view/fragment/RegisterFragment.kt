package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.interfaces.DialogListener
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.CustomSpinnerAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.RegisterAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.fragment.dialog.AddRegisterFragment
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.recent
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.LiquidacionViewModel
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.RegisterViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.customSwitch
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.DialogResumenLiquidacionBinding
import com.kasolution.aiohunterresources.databinding.FragmentRegisterBinding
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

class RegisterFragment : Fragment(), DialogListener {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val registerViewModel: RegisterViewModel by viewModels()
    private val liquidacionViewModel: LiquidacionViewModel by viewModels()
    private lateinit var adapter: RegisterAdapter
    private lateinit var adapterSpinner: CustomSpinnerAdapter
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var listRegister: ArrayList<register>
    private var itemRegistro: register? = null
    private var itemPosition = -1
    private var itemSpinnerPosition = -1
    private lateinit var preferencesCajaChica: SharedPreferences
    private var messageLoading = "Recuperando..."
    private lateinit var urlScript: String
    private lateinit var idSheet: String
    private lateinit var sheetName: String
    private var idSheetLiquidacion: String? = null
    private var sheetNameTemp = ""
    private lateinit var fileName: String
    private lateinit var sumaTotal: String

    //private var heigthResumen = 0
    private lateinit var listSheets: List<fileDetails>
    private var datosResumen: Map<String, Pair<Int, Double>>? = null
    private var urlId: urlId? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listRegister = ArrayList()
        listSheets = ArrayList()
        //urlId = urlId

        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        init()
        recuperarPreferencias()
        configSwipe()
        calcularTamanoResumenView()

        ///------------------------------------------------------
        binding.cswResumen.setOnSwitchChangedListener(object :
            customSwitch.OnSwitchChangedListener {
            override fun onSwitchChanged(isItemSelected: Boolean) {
                if (isItemSelected) {
                    // Acción cuando se selecciona "Montos"
                    procesarDatosResumen(0, datosResumen)
                } else {
                    // Acción cuando se selecciona "Cantidad de equipos"
                    procesarDatosResumen(1, datosResumen)
                }
            }
        })
        ///------------------------------------------------------


        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnLiquidar.setOnClickListener() {
            dialogConfirmLiquidacion()
        }
        binding.btnAdd.setOnClickListener()
        {
            adapter.limpiarSeleccion()
            abrirDialog()
        }

        binding.btnActualizar.setOnClickListener()
        {
            binding.llNoData.visibility = View.GONE
            registerViewModel.onRefresh(urlId!!)
        }
        registerViewModel.getRegister.observe(
            viewLifecycleOwner, Observer
            { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        adapter.limpiar()
                        val lista = respuesta.getOrNull()
                        lista?.let {
                            listRegister.addAll(it)
                            adapter.notifyDataSetChanged()
                            if (sheetNameTemp.isNotEmpty()) {
                                val editor = preferencesCajaChica.edit()
                                editor.putString("SHEETNAME", sheetNameTemp)
                                editor.apply()
                                sheetNameTemp.isEmpty()
                            }
                        }
                    } else {
                        // Si el resultado es un error, puedes manejarlo aquí
                        val exception = respuesta.exceptionOrNull()
                        exception?.let { ex ->
                            // Puedes mostrar el mensaje de error o manejarlo como prefieras
                            //Toast.makeText(context, "Errorr: ${ex.message}", Toast.LENGTH_LONG).show()
                            showMessageError(ex.message.toString())
                        }
                    }
                }

            })
        registerViewModel.insertarRegister.observe(
            viewLifecycleOwner, Observer
            { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        // Si el resultado es exitoso, obtenemos el registro
                        val register = respuesta.getOrNull()
                        register?.let { registro ->
                            // Agregar el registro a la lista
                            listRegister.add(0, registro)
                            adapter.notifyItemInserted(0)
                            lmanager.scrollToPosition(0)
                            debitarGasto(registro, "DEBITO")
                            saveRecent(
                                recent(
                                    icon = R.drawable.register,
                                    titulo = "Gastos -> Movilidad",
                                    detalle = registro.detalle,
                                    fecha = obtenerFechaActual()
                                )
                            )
                        }
                    } else {
                        val exception = respuesta.exceptionOrNull()
                        exception?.let { ex ->
                            showMessageError(ex.message.toString())
                        }
                    }
                }
            })

        registerViewModel.updateRegister.observe(
            viewLifecycleOwner, Observer
            { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val register = respuesta.getOrNull()
                        register?.let { registro ->
                            listRegister[itemPosition] = registro
                            adapter.notifyItemChanged(itemPosition)
                            debitarGasto(register, "COMPARAR")
                            saveRecent(
                                recent(
                                    icon = R.drawable.register,
                                    titulo = "Gastos -> Registro actualizado",
                                    detalle = registro.detalle,
                                    fecha = obtenerFechaActual()
                                )
                            )
                        }
                    } else {
                        val exception = respuesta.exceptionOrNull()
                        exception?.let { ex ->
                            showMessageError(ex.message.toString())
                        }
                    }
                }

            })

        registerViewModel.deleteRegister.observe(
            viewLifecycleOwner, Observer
            { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val register = respuesta.getOrNull()
                        register?.let { registro ->
                            listRegister.removeAt(itemPosition)
                            adapter.notifyItemRemoved(itemPosition)
                            debitarGasto(itemRegistro, "ABONAR")
                            saveRecent(
                                recent(
                                    icon = R.drawable.register,
                                    titulo = "Gastos -> Registro eliminado",
                                    detalle = registro,
                                    fecha = obtenerFechaActual()
                                )
                            )
                        }
                    } else {
                        val exception = respuesta.exceptionOrNull()
                        exception?.let { ex ->
                            showMessageError(ex.message.toString())
                        }
                    }
                }

            })
        registerViewModel.isloading.observe(
            viewLifecycleOwner, Observer
            {
                if (it) DialogProgress.show(requireContext(), messageLoading)
                else {
                    DialogProgress.dismiss()
                    if (listRegister.isEmpty()) {
                        binding.lottieAnimationView.setAnimation(R.raw.no_data_found)
                        binding.lottieAnimationView.playAnimation()
                        binding.llNoData.visibility = View.VISIBLE
                        binding.swipeRefresh.visibility = View.GONE
                        binding.btnLiquidar.visibility = View.INVISIBLE
                    } else {
                        binding.llNoData.visibility = View.GONE
                        binding.swipeRefresh.visibility = View.VISIBLE
                        binding.btnLiquidar.visibility = View.VISIBLE
                    }
                }
            })

        liquidacionViewModel.isloading.observe(
            viewLifecycleOwner, Observer
            {
                if (it) DialogProgress.show(requireContext(), messageLoading)
                else DialogProgress.dismiss()
            })

        registerViewModel.resumen.observe(viewLifecycleOwner, Observer { resumen ->
            Log.i("BladiDev", "datosResumen $datosResumen")
            datosResumen = resumen
            procesarDatosResumen(0, datosResumen)

        })

        registerViewModel.exception.observe(
            viewLifecycleOwner, Observer
            { error ->
                showMessageError(error)
                Log.i("BladiDevError", "Error $error")
            })
        liquidacionViewModel.downloadExcel.observe(
            viewLifecycleOwner, Observer
            { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val mensaje = respuesta.getOrNull()
                        mensaje?.let { sms ->
                            DialogUtils.dialogMessageResponse(
                                requireContext(),
                                "Archivo descargado correctamente en: $sms",
                                null
                            )
                        }
                    } else {
                        // Si el resultado es un error, puedes manejarlo aquí
                        val exception = respuesta.exceptionOrNull()
                        exception?.let { ex ->
                            // Puedes mostrar el mensaje de error o manejarlo como prefieras
                            showMessageError(ex.message.toString())
                        }
                    }
                }
            })
        liquidacionViewModel.insertLiquidacion.observe(
            viewLifecycleOwner, Observer
            { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val register = respuesta.getOrNull()
                        register?.let { liquidacion ->
                            if (liquidacion.download) {
                                messageLoading = "Descargando Excel"
                                //si el usurio activo la opcion de descarga del excel, aqui solicitamos la descarga
                                liquidacionViewModel.downloadExcel(
                                    liquidacion.downloadLink,
                                    liquidacion.concepto
                                )
                            } else DialogUtils.dialogMessageResponse(
                                requireContext(),
                                "Liquidacion Ingresado correctamente",
                                null
                            )
                            try {
                                // Obtener el JSON almacenado en SharedPreferences
                                val jsonString =
                                    preferencesCajaChica.getString("LIST_SHEET", "[]") ?: "[]"
                                Log.i("BladiDevBuscando", jsonString)
                                // Convertir la cadena JSON en un JSONArray
                                val jsonArray = JSONArray(jsonString)

                                // Obtener el item seleccionado del Spinner
                                val selectedItemPosition = binding.spSheets.selectedItemPosition
                                val selectedItem = jsonArray.getJSONObject(selectedItemPosition)
                                Log.i("BladiDevBuscando", selectedItemPosition.toString())
                                // Modificar el valor de "NOMBREREAL"
                                val nombreReal = selectedItem.getString("nombreReal")
                                val nuevoNombreReal =
                                    "$nombreReal->Enviado"  // Cambiar el texto como lo necesites

                                // Poner el nuevo valor en "NOMBREREAL"
                                selectedItem.put("nombreReal", nuevoNombreReal)

                                // Convertir el JSONArray modificado de nuevo a una cadena
                                val newJsonString = jsonArray.toString()

                                // Guardar el nuevo JSON en SharedPreferences
                                val editor = preferencesCajaChica.edit()
                                editor.putString("LIST_SHEET", newJsonString)
                                editor.apply()
                                val Sheets = preferencesCajaChica.getString("LIST_SHEET", null)
                                binding.btnAdd.isEnabled = false
                                Log.i("BladiDev", Sheets.toString())
                                recuperarPreferencias()
                                adapterSpinner.notifyDataSetChanged()
                                saveRecent(
                                    recent(
                                        icon = R.drawable.liquidacion,
                                        titulo = "Liquidacion -> Agregado",
                                        detalle = "Se agrego la liquidacion ${liquidacion.concepto}",
                                        fecha = obtenerFechaActual()
                                    )
                                )
                            } catch (e: Exception) {
                                Log.i("BladiDev", "ERROR " + e.message.toString())
                            }

                        }
                    }
                }
            })

        liquidacionViewModel.getIdSheetLiquidacion.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { fileAdd ->
                        if (fileAdd.id.isNotEmpty()) {
                            preferencesCajaChica.edit {
                                apply { putString("IDSHEETLIQUIDACION", fileAdd.id) }
                            }
                            messageLoading = "Recuperando..."
                            Toast.makeText(
                                requireContext(), "Ficha liquidaciones configurado",
                                Toast.LENGTH_SHORT
                            ).show()
                            recuperarPreferencias()
                        }
                    }


                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })

        if (urlScript.isEmpty()) {
            DialogUtils.dialogInput(requireContext(), "Ingrese codigo") { input ->
                if (input.isNotEmpty()) {
                    val editor = preferencesCajaChica.edit()
                    editor.putString("URL_SCRIPT", input)
                    editor.apply()
                } else
                    Toast.makeText(
                        context,
                        "Necesitas configurar tu caja chica",
                        Toast.LENGTH_SHORT
                    ).show()
                activity?.supportFragmentManager?.popBackStack()
            }
        } else if (idSheet.isEmpty() || sheetName.isEmpty()) {
            val fragment = FileFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contenedorCajaChica, fragment)
            fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
            fragmentTransaction.commit()
        } else {
            adapterSpinner = CustomSpinnerAdapter(requireContext(), listSheets)
            binding.spSheets.adapter = adapterSpinner

            //nos permite seleccionar un item con el nombre
            val position = listSheets.indexOfFirst { it.nombre == sheetName }
            binding.spSheets.setSelection(position)

            binding.spSheets.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = listSheets[position]
                    itemSpinnerPosition = position
                    sheetName = selectedItem.nombreReal
                    val tipoItem = identificarItem(listSheets[position].nombreReal)
                    when (tipoItem) {
                        "Enviado" -> {
                            DialogUtils.dialogMessageResponse(
                                requireContext(),
                                "El Registro ${listSheets[position].nombre} ya fue $tipoItem, solo se abrira en modo lectura.",
                                onPositiveClick = {
                                    binding.btnAdd.isEnabled = false
                                    cargarRegistro(selectedItem)
                                }
                            )
                        }

                        "Reembolsado" -> {
                            DialogUtils.dialogMessageResponse(
                                requireContext(),
                                "El Registro ${listSheets[position].nombre} ya fue $tipoItem, solo se abrira en modo lectura.",
                                onPositiveClick = {
                                    binding.btnAdd.isEnabled = false
                                    cargarRegistro(selectedItem)
                                }
                            )
                        }

                        "Editable" -> {

                            // Comprobar si el nombreReal ha cambiado
                            if (preferencesCajaChica.getString(
                                    "SHEETNAME",
                                    ""
                                ) != selectedItem.nombreReal
                            ) {
                                val editor = preferencesCajaChica.edit()
                                editor.putString("SHEETNAME", selectedItem.nombreReal)
                                editor.apply()
                            }
                            binding.btnAdd.isEnabled = true
                            cargarRegistro(selectedItem)
                        }
                    }
                    adapter.updateMyState(tipoItem)
                }
            }
        }
    }

    private fun calcularTamanoResumenView() {
        binding.llheadButtomSheet.post {
            val heigthResumen = binding.llheadButtomSheet.height  // Alto de la vista
            inicializarButtomSheet(heigthResumen)
        }
    }


    private fun cargarRegistro(selectedItem: fileDetails) {
        // Actualizar la URL y otros datos según el ítem seleccionado
        urlId = urlId(
            idScript = preferencesCajaChica.getString("URL_SCRIPT", "")
                .toString(),
            "",
            idSheet = preferencesCajaChica.getString("IDSHEET", "").toString(),
            sheetName = selectedItem.nombreReal
        )

        // Llamar a tu ViewModel o actualizar la UI
        registerViewModel.onCreate(urlId = urlId!!)

    }


    private fun identificarItem(texto: String): String {
        if (texto.contains("->Enviado")) {
            return "Enviado"
        } else if (texto.contains("->Reembolsado")) {
            return "Reembolsado"
        } else {
            return "Editable"
        }
    }

    private fun debitarGasto(register: register?, operacion: String) {
        var monto = register?.let { determinarGasto(it) }
        var montoItemSeleccionado = itemRegistro?.let { determinarGasto(it) }

        if (monto != null) {
            monto = monto.replace("S/ ", "")
            montoItemSeleccionado = montoItemSeleccionado?.replace("S/ ", "")
            when (operacion) {
                "DEBITO" -> {
                    val saldo = preferencesCajaChica.getString("SALDODISPONIBLE", "0")
                    val editor = preferencesCajaChica.edit()
                    var x = saldo!!.toDouble()
                    var y = monto.toDouble()
                    //val nuevoSaldo = saldo!!.toDouble() - monto.toDouble()
                    val nuevoSaldo = x - y
                    editor.putString("SALDODISPONIBLE", (nuevoSaldo).toString())
                    editor.apply()
                    Log.i("BladiDevRegister", "saldo disponible en register es $nuevoSaldo")
                }

                "ABONAR" -> {
                    val saldo = preferencesCajaChica.getString("SALDODISPONIBLE", "")
                    val editor = preferencesCajaChica.edit()
                    val nuevoSaldo = saldo!!.toDouble() + monto.toDouble()
                    editor.putString("SALDODISPONIBLE", (nuevoSaldo).toString())
                    editor.apply()
                    Log.i("BladiDevRegister", "saldo disponible en register es $nuevoSaldo")
                }

                "COMPARAR" -> {
                    val saldo = preferencesCajaChica.getString("SALDODISPONIBLE", "")
                    val editor = preferencesCajaChica.edit()
                    val saldoTemp = saldo!!.toDouble() + montoItemSeleccionado!!.toDouble()
                    val nuevoSaldo = saldoTemp - monto.toDouble()
                    editor.putString("SALDODISPONIBLE", (nuevoSaldo).toString())
                    editor.apply()
                }
            }
        }
    }

    private fun determinarGasto(register: register): String {
        return listOf(register.monto).firstOrNull { it.isNotEmpty() } ?: ""
    }

    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            DialogUtils.dialogQuestion(
                requireContext(),
                "Aviso",
                "Desea actualizar la lista?",
                positiveButtontext = "Si",
                negativeButtontext = "no",
                onPositiveClick = {
                    messageLoading = "Recuperando..."
                    registerViewModel.onRefresh(urlId!!)
                })
        }
    }


    private fun procesarDatosResumen(nroVista: Int, datosResumen: Map<String, Pair<Int, Double>>?) {
        //recuperamos los datos necesarios para asignarlos a los textview
        var TotalSuma = 0.0
        var totalRegistros = 0
        var s_movilidad = 0.0
        var c_movilidad = 0.0
        var s_alimentacion = 0.0
        var c_alimentacion = 0.0
        var s_alojamiento = 0.0
        var c_alojamiento = 0.0
        var s_otros = 0.0
        var c_otros = 0.0

        var c_smovilidad = 0
        var c_cmovilidad = 0
        var c_salimentacion = 0
        var c_calimentacion = 0
        var c_salojamiento = 0
        var c_calojamiento = 0
        var c_sotros = 0
        var c_cotros = 0
        datosResumen!!.forEach { (campo, datos) ->
            val (cantidad, suma) = datos
            when (campo) {
                "Total Registros" -> totalRegistros = cantidad
                "Total Suma" -> TotalSuma = suma
                "s_movilidad" -> {
                    s_movilidad = suma
                    c_smovilidad = cantidad
                }

                "c_movilidad" -> {
                    c_movilidad = suma
                    c_cmovilidad = cantidad
                }

                "s_alimentacion" -> {
                    s_alimentacion = suma
                    c_salimentacion = cantidad
                }

                "c_alimentacion" -> {
                    c_alimentacion = suma
                    c_calimentacion = cantidad
                }

                "s_alojamiento" -> {
                    s_alojamiento = suma
                    c_salojamiento = cantidad
                }

                "c_alojamiento" -> {
                    c_alojamiento = suma
                    c_calojamiento = cantidad
                }

                "s_otros" -> {
                    s_otros = suma
                    c_sotros = cantidad
                }

                "c_otros" -> {
                    c_otros = suma
                    c_cotros = cantidad
                }
            }
        }
        binding.lblCant.text = "$totalRegistros Registros"
        binding.lblsuma.text = "S/ ${String.format("%.2f", TotalSuma)}"
        sumaTotal = "S/ ${String.format("%.2f", TotalSuma)}"
        if (nroVista == 0) {
            binding.lblMSS.text = "S/ $s_movilidad"
            binding.lblMCS.text = "S/ $c_movilidad"
            binding.lblASS.text = "S/ $s_alimentacion"
            binding.lblACS.text = "S/ $c_alimentacion"
            binding.lblHSS.text = "S/ $s_alojamiento"
            binding.lblHCS.text = "S/ $c_alojamiento"
            binding.lblOSS.text = "S/ $s_otros"
            binding.lblOCS.text = "S/ $c_otros"
            binding.lblMT.text = "S/ ${(s_movilidad + c_movilidad)}"
            binding.lblAT.text = "S/ ${(s_alimentacion + c_alimentacion)}"
            binding.lblHT.text = "S/ ${(s_alojamiento + c_alojamiento)}"
            binding.lblOT.text = "S/ ${(s_otros + c_otros)}"
            binding.lblTSS.text = "S/ ${(s_movilidad + s_alimentacion + s_alojamiento + s_otros)}"
            binding.lblTCS.text = "S/ ${(c_movilidad + c_alimentacion + c_alojamiento + c_otros)}"
            binding.lblTT.text = "S/ ${String.format("%.2f", TotalSuma)}"
        } else if (nroVista == 1) {
            binding.lblMSS.text = "$c_smovilidad"
            binding.lblMCS.text = "$c_cmovilidad"
            binding.lblASS.text = "$c_salimentacion"
            binding.lblACS.text = "$c_calimentacion"
            binding.lblHSS.text = "$c_salojamiento"
            binding.lblHCS.text = "$c_calojamiento"
            binding.lblOSS.text = "$c_sotros"
            binding.lblOCS.text = "$c_cotros"
            binding.lblMT.text = "${(c_smovilidad + c_cmovilidad)}"
            binding.lblAT.text = "${(c_salimentacion + c_calimentacion)}"
            binding.lblHT.text = "${(c_salojamiento + c_calojamiento)}"
            binding.lblOT.text = "${(c_sotros + c_cotros)}"
            binding.lblTSS.text = "${(c_smovilidad + c_salimentacion + c_salojamiento + c_sotros)}"
            binding.lblTCS.text = "${(c_cmovilidad + c_calimentacion + c_calojamiento + c_cotros)}"
            binding.lblTT.text = "$totalRegistros"
        }
    }


    private fun recuperarPreferencias() {
        urlScript = preferencesCajaChica.getString("URL_SCRIPT", "").toString()
        idSheet = preferencesCajaChica.getString("IDSHEET", "").toString()
        fileName = preferencesCajaChica.getString("FILENAME", "").toString()
        sheetName = preferencesCajaChica.getString("SHEETNAME", "").toString()
        idSheetLiquidacion = preferencesCajaChica.getString("IDSHEETLIQUIDACION", null)
        if (sheetName.contains("->")) {
            val part = sheetName.split("->")
            sheetName = part[0]
            sheetNameTemp = part[1]
        }
        val Sheets = preferencesCajaChica.getString("LIST_SHEET", null)
        listSheets = llenarLista(Sheets)
    }


    private fun llenarLista(sheets: String?): List<fileDetails> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<fileDetails>>() {}.type
        return gson.fromJson(sheets, type)
    }

    private fun init() {
        lmanager = LinearLayoutManager(context)
        adapter = RegisterAdapter(
            listaRecibida = listRegister,
            onClickListener = { itemRegister, action, position ->
                onItemSelected(
                    itemRegister,
                    action,
                    position
                )
            },
            onClickDeselect = { })
        binding.RVRegistros.layoutManager = lmanager
        binding.RVRegistros.adapter = adapter
    }

    private fun onItemDelete(itemRegister: register, position: Int) {
        DialogUtils.dialogQuestion(
            requireContext(),
            "Aviso",
            "Esta seguro que desea eliminar este registro?",
            positiveButtontext = "Si",
            negativeButtontext = "no",
            onPositiveClick = {
                messageLoading = "Eliminando..."
                itemPosition = position
                itemRegistro = itemRegister
                registerViewModel.deleteRegister(urlId!!, itemRegister)
            })
    }

    private fun onItemUpdate(itemRegister: register, position: Int) {
        itemPosition = position
        itemRegistro = itemRegister
        abrirDialog(itemRegister)
    }

    private fun onItemSelected(itemRegister: register, action: Int, position: Int) {
        adapter.limpiarSeleccion()
        when (action) {
            1 -> onItemUpdate(itemRegister, position)
            2 -> onItemDelete(itemRegister, position)
        }
    }


    private fun abrirDialog(register: register? = null) {
        val dialogFragment = AddRegisterFragment()
        dialogFragment.isCancelable = false
        //dialogFragment.getlistamarcas(marcas!!) //desabilitado temporal
        dialogFragment.listener = this
        val args = Bundle().apply {
            putSerializable("register", register)
        }
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, "AddImageResourceDialogFragment")
    }

    private fun dialogConfirmLiquidacion() {
        if (idSheetLiquidacion != null) {
            if (itemSpinnerPosition != -1) {
                if (identificarItem(listSheets[itemSpinnerPosition].nombreReal) != "Editable") {
                    DialogUtils.dialogMessageResponse(
                        requireContext(),
                        "El Registro '${listSheets[itemSpinnerPosition].nombre}' ya fue liquidado.\nNo se puede volver a generar la liquidacion, consulte el estado de su liquidacion en el apartado de liquidaciones.",
                        null
                    )
                } else {
                    // Cargar la animación
                    val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce3)

                    // Inflar la vista con ViewBinding
                    val binding =
                        DialogResumenLiquidacionBinding.inflate(LayoutInflater.from(context))

                    // Crear el AlertDialog
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setView(binding.root) // Usamos binding.root aquí
                    val dialog = builder.create()
                    dialog.setCancelable(true)
                    dialog.show()

                    // Aplicar la animación
                    binding.root.animation = anim1
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    binding.lblArchivo.text = fileName
                    binding.lblConcepto.text = sheetName
                    binding.lblMonto.text = sumaTotal
                    // Configurar el listener para el botón
                    binding.btnEnviar.setOnClickListener {
                        val urlIdtemp = urlId(
                            idScript = preferencesCajaChica.getString("URL_SCRIPT", "").toString(),
                            "",
                            idSheet = preferencesCajaChica.getString("IDSHEETLIQUIDACION", "")
                                .toString(),
                            sheetName = "LIQUIDACIONES"
                        )
                        val install =
                            if (binding.etnroInstalaciones.text.isNotEmpty() && binding.etnroInstalaciones.text.toString()
                                    .toIntOrNull() != null
                            ) binding.etnroInstalaciones.text.toString().toInt() else 0
                        val desinstall =
                            if (binding.etnroDesinstalaciones.text.isNotEmpty() && binding.etnroDesinstalaciones.text.toString()
                                    .toIntOrNull() != null
                            ) binding.etnroDesinstalaciones.text.toString().toInt() else 0
                        val chequeos =
                            if (binding.etnroChequeos.text.isNotEmpty() && binding.etnroChequeos.text.toString()
                                    .toIntOrNull() != null
                            ) binding.etnroChequeos.text.toString().toInt() else 0
                        messageLoading = "Enviando..."
                        liquidacionViewModel.insertLiquidacion(
                            urlIdtemp,
                            liquidacion(
                                id = "",
                                fecha = obtenerFechaActual(),
                                archivo = idSheet,
                                concepto = sheetName.toString(),
                                monto = sumaTotal.toString(),
                                estado = "Enviado",
                                downloadLink = "",
                                download = binding.cbdownload.isChecked
                            ), arrayListOf(install, desinstall, chequeos)
                        )
                        dialog.dismiss()
                    }
                }
            }
        } else {
            DialogUtils.dialogQuestion(
                requireContext(),
                "AVISO",
                "Requiere configurar ficha liquidaciones\n¿Desea configurarlo ahora?",
                "Si",
                "No",
                onPositiveClick = {
                    messageLoading = "Configurando..."
                    liquidacionViewModel.createLiquidacionSheet(
                        urlId(
                            idScript = preferencesCajaChica.getString("URL_SCRIPT", "").toString(),
                            idFile = preferencesCajaChica.getString("IDFILE", "").toString(),
                            idSheet = "", sheetName = ""
                        )
                    )
                }
            )
        }

    }


    override fun onDataCollected(register: register) {
        messageLoading = "Guardando..."
        registerViewModel.insertRegister(urlId!!, register)
    }

    override fun onDataCollectedUpdate(register: register) {
        messageLoading = "Modificando..."
        registerViewModel.updateRegister(urlId!!, register)
    }

    private fun inicializarButtomSheet(height: Int) {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = height
            state = BottomSheetBehavior.STATE_COLLAPSED

            // Agrega el BottomSheetCallback
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Manejar cambios de estado si es necesario
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            // Cuando el BottomSheet está colapsado
                            binding.llResumen.visibility = View.VISIBLE // Muestra el botón
                            binding.llswitchOption.visibility = View.GONE
                            // Otras acciones, como cambiar el color de fondo o la opacidad de otros elementos
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            // Cuando el BottomSheet está expandido
                            binding.llResumen.visibility = View.GONE // Oculta el botón
                            binding.llswitchOption.visibility = View.VISIBLE
                            // Otras acciones, como deshabilitar ciertos botones

                            //asignamos el tamaño al resumen
                            val layoutParams = binding.bottomSheet.layoutParams
                            Log.i("BladiDev","tamaño del bottonSheet ${ViewGroup.LayoutParams.WRAP_CONTENT}")
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            binding.bottomSheet.layoutParams = layoutParams
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            // Cuando el BottomSheet está oculto
                            // Puedes hacer otras acciones aquí, si lo deseas
                        }
                    }

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.llResumen.alpha = 1 - slideOffset
                    binding.llswitchOption.alpha = slideOffset
                }

            })
        }
    }

    private fun obtenerFechaActual(): String {
        val fechaActual = Calendar.getInstance()  // Obtiene la fecha actual del sistema
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(fechaActual.time)  // Formatea la fecha y la devuelve como cadena
    }

    private fun saveRecent(recent: recent) {
        val editor = preferencesCajaChica.edit()
        val recentListJson = preferencesCajaChica.getString("RECENT_DATA", null)
        val recentList = mutableListOf<String>()
        // Convertir la lista actual de JSON a objetos Recent (si existe)
        recentListJson?.let {
            recentList.addAll(Gson().fromJson(it, Array<String>::class.java).toList())
        }
        // Agregar el nuevo objeto a la lista y limitar a 10 elementos
        recentList.add(0, Gson().toJson(recent))
        if (recentList.size > 20) {
            recentList.removeAt(recentList.size - 1)
        }
        // Convertir la lista actualizada a JSON y guardarla
        editor.putString("RECENT_DATA", Gson().toJson(recentList))
        editor.apply()
    }

    private fun getRecentList(recentList: String?): List<recent> {
        return recentList?.let {
            Gson().fromJson(it, Array<String>::class.java)
                .map { json -> Gson().fromJson(json, recent::class.java) }.toList()
        } ?: emptyList()
    }

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            requireContext(),
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
        )
    }
}