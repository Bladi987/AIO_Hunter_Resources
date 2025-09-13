package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.FileDetailsAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.recent
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.FileDetailsViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.ToastUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.DialogNewSheetBinding
import com.kasolution.aiohunterresources.databinding.FragmentFileDetailsBinding
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale


class FileDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFileDetailsBinding
    private val fileDetailsViewModel: FileDetailsViewModel by viewModels()
    private lateinit var adapter: FileDetailsAdapter
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var listFileDetails: ArrayList<fileDetails>
    private var files: file? = null
    private var urlId: urlId? = null
    var sheetSelected = ""
    var sheetSelectedPreferences = ""
    private var itemPosition = -1
    private var insert = false
    private var messageLoading = "Recuperando..."
    private var listaSheet: ArrayList<fileDetails>? = null
    private lateinit var fileDetail: fileDetails
    private var nameTecnico: String? = null
    private var identifacionUser:String?=null
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFileDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesUser =
            requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        listFileDetails = ArrayList()

        initRecycler()
        recuperarDatosRecibidos()
        recuperarPreferencias()
        configSwipe()

        binding.tvTitle.text = files?.nombre
        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnAction1.setOnClickListener() {
            if (binding.btnAction2.visibility == View.VISIBLE) {
                DialogUtils.dialogQuestion(
                    requireContext(),
                    "Advertencia",
                    "Desea eliminar la hoja?",
                    positiveButtontext = "Si",
                    negativeButtontext = "no",
                    onPositiveClick = {
                        messageLoading = "Eliminando..."
                        fileDetailsViewModel.onDelete(urlId!!, fileDetail)
                    }
                )
            } else dialogFile()
        }
        binding.btnAction2.setOnClickListener() {
            dialogFile(fileDetail.nombre)
        }
        binding.btnActualizar.setOnClickListener() {
            binding.llNoData.visibility = View.GONE
            fileDetailsViewModel.onRefresh(urlId!!)
        }
        fileDetailsViewModel.onCreate(urlId!!)
        fileDetailsViewModel.FileDetailsModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { listSheet ->
                        adapter.limpiar()
                        listaSheet = listSheet
                        listFileDetails.addAll(listSheet)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        fileDetailsViewModel.insertarFileSheet.observe(viewLifecycleOwner, Observer { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val data = respuesta.getOrNull()
                        data?.let {fileSheet ->
                            if (insert) {
                                if (fileSheet.nombre.isNotEmpty() && fileSheet.nombreReal.isNotEmpty()) {
                                    listFileDetails.add(0, fileSheet)
                                    adapter.notifyItemInserted(0)
                                    lmanager.scrollToPosition(0)
                                    listaSheet!!.add(0, fileSheet)
                                    val gson = Gson()
                                    val json = gson.toJson(listaSheet)
                                    val editor = preferencesCajaChica.edit()
                                    editor.putString("LIST_SHEET", json.toString())
                                    editor.apply()
                                    saveRecent(
                                        recent(
                                            icon = R.drawable.carpeta,
                                            titulo = "Archivos -> Hoja creada",
                                            detalle = fileSheet.nombre,
                                            fecha = obtenerFechaActual()
                                        )
                                    )
                                } else DialogUtils.dialogMessageResponse(
                                    requireContext(),
                                    "Ocurrio un error al crear la hoja"
                                )
                                insert = false
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
        fileDetailsViewModel.updateFileSheet.observe(viewLifecycleOwner, Observer { result->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { fileSheet ->
                        if (fileSheet.nombre.isNotEmpty() && fileSheet.nombreReal.isNotEmpty()) {
                            listFileDetails[itemPosition] = fileSheet
                            adapter.notifyItemChanged(itemPosition)
                            val gson = Gson()
                            val json = gson.toJson(listFileDetails)
                            val editor = preferencesCajaChica.edit()
                            editor.putString("LIST_SHEET", json.toString())
                            editor.apply()
                            saveRecent(
                                recent(
                                    icon = R.drawable.carpeta,
                                    titulo = "Archivos -> Hoja Modificada",
                                    detalle = "Nuevo nombre ${fileSheet.nombre}",
                                    fecha = obtenerFechaActual()
                                )
                            )
                        } else DialogUtils.dialogMessageResponse(
                            requireContext(),
                            "Ocurrio un error al intentar modificar el nombre"
                        )
                    }
                }else{
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        fileDetailsViewModel.deleteFileSheet.observe(viewLifecycleOwner, Observer { result-> //fileSheet ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { fileSheet ->
                        val nombreitem = listFileDetails[itemPosition].nombre
                        listFileDetails.removeAt(itemPosition)
                        adapter.notifyItemRemoved(itemPosition)
                        updatePreferences(fileSheet)
                        saveRecent(
                            recent(
                                icon = R.drawable.carpeta,
                                titulo = "Archivos -> Hoja eliminada",
                                detalle = nombreitem,
                                fecha = obtenerFechaActual()
                            )
                        )
                    }
                }else{
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        fileDetailsViewModel.isloading.observe(viewLifecycleOwner, Observer { cargando ->
            adapter.limpiarSeleccion()
            if (cargando) DialogProgress.show(requireContext(), messageLoading)
            else {
                DialogProgress.dismiss()
                if (listFileDetails.isEmpty()) {
                    binding.lottieAnimationView.setAnimation(R.raw.no_data_found)
                    binding.lottieAnimationView.playAnimation()
                    binding.llNoData.visibility = View.VISIBLE
                    binding.swipeRefresh.visibility = View.GONE
                } else {
                    binding.llNoData.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                }
            }
            MostrarActionIcon(false)
        })
        fileDetailsViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
    }

    private fun updatePreferences(nameSheet: String) {
        recuperarPreferencias()
        for (i in listaSheet?.indices!!) {
            val sheet = listaSheet!![i].nombre
            if (sheet == nameSheet) {
                listaSheet!!.removeAt(i)
                if (nameSheet == sheetSelectedPreferences) {
                    //si la hoja eliminada era la predeterminada se tiene que cambiar al siguiente en la lista
                    val editor = preferencesCajaChica.edit()
                    editor.putString("SHEETNAME", listaSheet!![0].nombre)
                    editor.apply()
                }
                //actualizar lista de sheet en las preferencias
                val gson = Gson()
                val json = gson.toJson(listaSheet)
                val editor = preferencesCajaChica.edit()
                editor.putString("SHEETNAME", listaSheet!![0].nombre)
                editor.putString("LIST_SHEET", json.toString())
                editor.apply()
                break
            }
        }

    }

    private fun MostrarActionIcon(mostrar: Boolean) {
        if (mostrar) {
            binding.btnAction2.visibility = View.VISIBLE
            binding.imgAdd.setImageResource(R.drawable.ic_delete)
            binding.imgAdd.setColorFilter(Color.parseColor("#FFFFFF"))
        } else {
            binding.btnAction2.visibility = View.INVISIBLE
            binding.imgAdd.setImageResource(R.drawable.add_file)
            binding.imgAdd.clearColorFilter()
        }
    }

    private fun llenarListaSheet(sheets: String?): ArrayList<fileDetails> {
        if (sheets.isNullOrEmpty()) {
            return ArrayList() // Devuelve una lista vacía si sheets es null o vacío
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<fileDetails>>() {}.type
        return gson.fromJson(sheets, type)
    }

    private fun recuperarPreferencias() {
        urlId =
            urlId(preferencesCajaChica.getString("URL_SCRIPT", "").toString(), "", files!!.id, "")
        sheetSelectedPreferences = preferencesCajaChica.getString("SHEETNAME", "").toString()
        val sheets = preferencesCajaChica.getString("LIST_SHEET", null)
        nameTecnico =
            preferencesUser.getString("NAME", "") + " " + preferencesUser.getString("LASTNAME", "")
        identifacionUser=preferencesUser.getString("IDENTIFICATION",null)
        //if (!sheets.isNullOrEmpty() && sheets!="null")
        listaSheet = llenarListaSheet(sheets)

    }

    private fun recuperarDatosRecibidos() {
        arguments?.let {
            files = it.getSerializable("lista")!! as file
        }
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
                    fileDetailsViewModel.onRefresh(urlId!!)
                })

        }
    }

    private fun initRecycler() {
        lmanager = LinearLayoutManager(context)
        adapter = FileDetailsAdapter(
            listaRecibida = listFileDetails,
            onClickListener = { fileDetails, action, position ->
                onItemSelected(
                    fileDetails,
                    action,
                    position
                )
            },
            onClickDeselect = { MostrarActionIcon(false) })
        binding.recyclerview1.layoutManager = lmanager
        binding.recyclerview1.adapter = adapter
    }


    private fun onItemSelected(fileDetails: fileDetails, action: Int, position: Int) {
        itemPosition = position
        when (action) {
            1 -> {
                if (fileDetails.nombreReal.contains("->")) {
                    sheetSelected = fileDetails.nombre + "->" + sheetSelectedPreferences
                } else sheetSelected = fileDetails.nombre
                val gson = Gson()
                val json = gson.toJson(listaSheet)
                val editor = preferencesCajaChica.edit()
                editor.putString("IDSHEET", files?.id)
                editor.putString("FILENAME", files?.nombre)
                editor.putString("SHEETNAME", sheetSelected)
                editor.putString("LIST_SHEET", json.toString())
                editor.apply()
                val registerFragment = RegisterFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.contenedorCajaChica, registerFragment)
                fragmentTransaction.addToBackStack("FD") // Para agregar el fragmento a la pila de retroceso
                fragmentManager.popBackStack("H", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                fragmentTransaction.commit()
            }

            2 -> {
                fileDetail = fileDetails
                MostrarActionIcon(true)
            }
            3->{
                MostrarActionIcon(false)
                DialogUtils.dialogMessageResponse(requireContext(), "Este archivo no es posible editar por este medio, ingrese a su plataforma de drive para realizar las modificaciones necesarias")
            }
        }

    }

    private fun dialogFile(nameSheet: String? = "", position: Int = -1) {
        // Cargar animación
        val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce3)
        // Usar ViewBinding para inflar el layout del diálogo
        val binding = DialogNewSheetBinding.inflate(LayoutInflater.from(context))
        binding.llinclude.tvFechaInicio.text=obtenerFecha()
        binding.llinclude.tvFechaFin.text=obtenerFecha()
        // Análisis y precarga
        if (!nameSheet.isNullOrEmpty()) {
            // Modificar el nombre de la hoja
            binding.tvtitle.text = "Modificar Nombre"
            binding.llinclude.tvoldName.visibility=View.VISIBLE
            binding.llinclude.tvoldName.text=nameSheet
            binding.btnCrear.text = "Modificar"
            binding.llinclude.tvname.hint="Ingrese Nuevo Nombre"
        }

        // Crear el diálogo y mostrarlo
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        // Establecer animación
        binding.root.animation = anim1
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.llinclude.btnFechaInicio.setOnClickListener {
            showDatePickerDialog(binding.llinclude.tvFechaInicio)
        }
        binding.llinclude.btnFechaFin.setOnClickListener {
            showDatePickerDialog(binding.llinclude.tvFechaFin)
        }
        // Lógica de los botones
        binding.btnCrear.setOnClickListener {

            if (binding.llinclude.tvname.text.toString().isEmpty()) {
                binding.llinclude.tvname.error = "Ingrese un nombre"
            } else if (binding.llinclude.tvDestino.text.isEmpty()) {
                binding.llinclude.tvDestino.error = "Ingrese un destino"
            } else if (binding.llinclude.tvFechaInicio.text.isEmpty()) {
                binding.llinclude.tvFechaInicio.error = "Ingrese fecha"
            } else {
                if (!nameSheet.isNullOrEmpty()) {
                    // Acción de actualización
                    messageLoading = "Modificando..."
                    fileDetailsViewModel.onUpdate(
                        urlId!!,
                        fileDetails(
                            binding.llinclude.tvname.text.toString().trim(),
                            binding.llinclude.tvoldName.text.toString().trim()
                        ),
                        listOf(
                            nameTecnico.toString(),
                            identifacionUser.toString(),
                            binding.llinclude.tvDestino.text.toString().trim(),
                            binding.llinclude.tvFechaInicio.text.toString().trim(),
                            binding.llinclude.tvFechaFin.text.toString().trim()
                        )
                    )
                    dialog.dismiss()
                } else {
                    // Acción de inserción
                    messageLoading = "Creando..."
                    fileDetailsViewModel.onInsert(
                        urlId!!,
                        fileDetails(
                            "",
                            binding.llinclude.tvname.text.toString().trim()
                        ),
                        listOf(
                            nameTecnico.toString(),
                            identifacionUser.toString(),
                            binding.llinclude.tvDestino.text.toString().trim(),
                            binding.llinclude.tvFechaInicio.text.toString().trim(),
                            binding.llinclude.tvFechaFin.text.toString().trim()
                        )
                    )
                    dialog.dismiss()
                    insert = true
                }
                MostrarActionIcon(false)
            }
        }
        // Establecer un listener para cuando el diálogo se cierre
        dialog.setOnDismissListener {
            //MostrarActionIcon(false)
        }
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

    private fun obtenerFechaActual(): String {
        val fechaActual = Calendar.getInstance()  // Obtiene la fecha actual del sistema
        val formato =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Define el formato deseado
        return formato.format(fechaActual.time)  // Formatea la fecha y la devuelve como cadena
    }

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            requireContext(),
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
        )
    }
    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                textView.text = formattedDate
            }, year, month, day)

        datePickerDialog.show()
    }
    private fun obtenerFecha(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH) + 1)
        val dayOfMonth =
            String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.DAY_OF_MONTH))
        return "$dayOfMonth/$month/$year"
    }
}