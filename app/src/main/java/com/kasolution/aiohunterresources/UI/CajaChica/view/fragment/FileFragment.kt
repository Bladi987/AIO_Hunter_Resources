package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.FileAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.FileViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.databinding.FragmentFileBinding
import java.io.Serializable
import java.util.ArrayList
import com.google.gson.Gson
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.recent
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.DialogNewDocumentBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FileFragment : Fragment() {
    private lateinit var binding: FragmentFileBinding
    private val fileViewModel: FileViewModel by viewModels()
    private lateinit var adapter: FileAdapter
    private lateinit var glmanager: GridLayoutManager
    private lateinit var listFile: ArrayList<file>
    var fileSelected = ""
    private var urlId: urlId? = null
    private var itemPosition = -1
    private lateinit var files: file
    private var messageLoading = "Recuperando..."
    private var nameTecnico: String? = null
    private var identifacionUser:String?=null
    private var insert = false
    private var loaded = false
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesUser =
            requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        listFile = ArrayList()
        init()
        recuperarPreferencias()
        configSwipe()
        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
            //requireActivity().finish()

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
                        fileViewModel.onDeleteDocument(urlId!!, files)
                    }
                )
            } else dialogFile()
        }
        binding.btnAction2.setOnClickListener() {
            dialogFile(files.nombre)
        }
        binding.btnActualizar.setOnClickListener() {
            binding.llNoData.visibility = View.GONE
            fileViewModel.onRefresh(urlId!!)
        }
        fileViewModel.onCreate(urlId!!)
        fileViewModel.isloading.observe(viewLifecycleOwner, Observer {
            adapter.limpiarSeleccion()
            if (it) DialogProgress.show(requireContext(), "Recuperando...")
            else {
                DialogProgress.dismiss()
                if (listFile.isEmpty()) {
                    if (!loaded) {
                        binding.lottieAnimationView.setAnimation(R.raw.no_data_found)
                        binding.lottieAnimationView.playAnimation()
                        binding.llNoData.visibility = View.VISIBLE
                        binding.swipeRefresh.visibility = View.GONE
                    }
                } else {
                    binding.llNoData.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                }
            }
        })
        fileViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
        fileViewModel.FileModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    adapter.limpiar()
                    val lista = respuesta.getOrNull()
                    lista?.let { listaArchivos ->
                        listFile.addAll(listaArchivos)
                        adapter.notifyDataSetChanged()
                        //Guardar en preferences la lista de archivos
                        guardarListaArchivos(listaArchivos)
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })

        fileViewModel.createDocument.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { fileAdd ->
                        if (fileAdd.id.isNotEmpty() && fileAdd.nombre.isNotEmpty()) {
                            listFile.add(0, fileAdd)
                            adapter.notifyItemInserted(0)
                            glmanager.scrollToPosition(0)
                            saveRecent(
                                recent(
                                    icon = R.drawable.carpeta,
                                    titulo = "Archivos -> Documentos creado",
                                    detalle = fileAdd.nombre,
                                    fecha = obtenerFechaActual()
                                )
                            )
                        } else DialogUtils.dialogMessageResponse(
                            requireContext(),
                            "Ocurrio un error al crear la hoja"
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
        fileViewModel.updateDocument.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { fileUpdate ->
                        if (fileUpdate.id.isNotEmpty() && fileUpdate.nombre.isNotEmpty()) {
                            listFile[itemPosition] = fileUpdate
                            adapter.notifyItemChanged(itemPosition)
                            saveRecent(
                                recent(
                                    icon = R.drawable.carpeta,
                                    titulo = "Archivos -> Documentos Modificado",
                                    detalle = "Nuevo nombre ${fileUpdate.nombre}",
                                    fecha = obtenerFechaActual()
                                )
                            )
                        } else DialogUtils.dialogMessageResponse(
                            requireContext(),
                            "Ocurrio un error al intentar modificar el nombre"
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
        fileViewModel.deleteDocument.observe(
            viewLifecycleOwner,
            Observer { result -> //fileDelete ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val data = respuesta.getOrNull()
                        data?.let { fileDelete ->
                            val nombreitem = listFile[itemPosition].nombre
                            listFile.removeAt(itemPosition)
                            adapter.notifyItemRemoved(itemPosition)
                            saveRecent(
                                recent(
                                    icon = R.drawable.carpeta,
                                    titulo = "Archivos -> Documentos eliminado",
                                    detalle = nombreitem,
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
    }

    private fun recuperarPreferencias() {
        urlId = urlId(
            preferencesCajaChica.getString("URL_SCRIPT", "").toString(),
            preferencesCajaChica.getString("IDFILE", "").toString(),
            "",
            ""
        )
        nameTecnico =
            preferencesUser.getString("NAME", "") + " " + preferencesUser.getString("LASTNAME", "")
        identifacionUser=preferencesUser.getString("IDENTIFICATION",null)
        Log.i("BladiDev","identificacion $identifacionUser")
    }

    private fun guardarListaArchivos(listaArchivos: ArrayList<file>?) {
        val gson = Gson()
        val json = gson.toJson(listaArchivos)
        val editor = preferencesCajaChica.edit()
        editor.putString("LIST_FILE", json).apply()
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
                    fileViewModel.onRefresh(urlId!!)
                })
        }
    }

    private fun init() {
        val columnWidthDp = 400
        val columns = resources.displayMetrics.widthPixels / columnWidthDp
        glmanager = GridLayoutManager(context, columns)
        adapter = FileAdapter(
            listaRecibida = listFile,
            onClickListener = { itemFile, action, position ->
                onItemSelected(
                    itemFile,
                    action,
                    position
                )
            },
            onClickDeselect = { MostrarActionIcon(false) })
        binding.recyclerview1.layoutManager = glmanager
        binding.recyclerview1.adapter = adapter

    }

    private fun onItemSelected(file: file, action: Int, position: Int) {
        itemPosition = position
        when (action) {
            1 -> {
                fileSelected = file.nombre
                val detailsFragment = FileDetailsFragment()
                val args = Bundle().apply {
                    putSerializable("lista", objectSend())
                }
                detailsFragment.arguments = args

                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.contenedorCajaChica, detailsFragment)
                fragmentTransaction.addToBackStack("F") // Para agregar el fragmento a la pila de retroceso
                loaded=true
                fragmentTransaction.commit()
            }

            2 -> {
                files = file
                MostrarActionIcon(true)
            }
        }
    }

    private fun dialogFile(nameDocument: String? = "", position: Int = -1) {
        // Cargar animación
        val anim1 = AnimationUtils.loadAnimation(context, R.anim.bounce3)
        // Usar ViewBinding para inflar el layout del diálogo
        val binding = DialogNewDocumentBinding.inflate(LayoutInflater.from(context))
        // Análisis y precarga
        if (!nameDocument.isNullOrEmpty()) {
            // Modificar el nombre de la hoja
            binding.tvtitle.text = "Modificar Nombre"
            binding.tvoldName.visibility = View.VISIBLE
            binding.tvoldName.text = nameDocument
            binding.btnCrear.text = "Modificar"
            binding.tvname.hint = "Ingrese Nuevo Nombre"
            binding.llAdicional.visibility = View.GONE
        }
        var mostrarAdicional = false
        var concentimiento = false
        var datosCompletos = false
        // Crear el diálogo y mostrarlo
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        // Establecer animación
        binding.root.animation = anim1
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Lógica de los botones
        binding.btnAdicional.setOnClickListener() {
            binding.llAdicionales.visibility = View.VISIBLE
            binding.llAdicional.visibility = View.GONE
            binding.llNotificacion.visibility = View.GONE
            binding.llinclude.tvFechaInicio.text=obtenerFecha()
            binding.llinclude.tvFechaFin.text=obtenerFecha()
            mostrarAdicional = true
        }
        binding.cbConcentimiento.setOnClickListener() {
            concentimiento = true
            binding.btnCrear.isEnabled = true
        }
        binding.llinclude.btnFechaInicio.setOnClickListener {
            showDatePickerDialog(binding.llinclude.tvFechaInicio)
        }
        binding.llinclude.btnFechaFin.setOnClickListener {
            showDatePickerDialog(binding.llinclude.tvFechaFin)
        }
        binding.btnCrear.setOnClickListener {
            if (binding.tvname.text.toString().isEmpty()) {
                binding.tvname.error = "Ingrese un nombre"
            } else {
                if (!nameDocument.isNullOrEmpty()) {
                    // Acción de actualización
                    messageLoading = "Modificando..."
                    fileViewModel.onUpdateDocument(
                        urlId!!,
                        file(
                            files.id,
                            binding.tvname.text.toString().trim(),
                        )
                    )
                    dialog.dismiss()
                } else {
                    // Acción de inserción
                    if (concentimiento) {
                        // Si todos los campos son válidos, continuar con el proceso
                        messageLoading = "Creando..."
                        fileViewModel.onCreateDocument(
                            urlId!!,
                            file(
                                "",
                                binding.tvname.text.toString().trim()
                            ),
                            listOf("Hoja 1", nameTecnico.toString(), identifacionUser.toString(), "","","")
                        )
                        dialog.dismiss()
                        insert = true
                    } else {
                        if (mostrarAdicional) {
                            //los campos adicionales esta visibles, evaluar si estan con datos
                            val editTexts =
                                listOf(binding.llinclude.tvname, binding.llinclude.tvDestino)
                            val isValid = validarEdittext(editTexts)
                            if (isValid) {
                                // Si todos los campos son válidos, continuar con el proceso
                                messageLoading = "Creando..."
                                fileViewModel.onCreateDocument(
                                    urlId!!,
                                    file(
                                        "",
                                        binding.tvname.text.toString().trim()
                                    ),
                                    listOf(
                                        binding.llinclude.tvname.text.toString().trim(),
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
                        } else {
                            //si los campos adicionales no estan visibles, crear la hoja sin datos adicionales pero
                            // mostrar una notificacion que se recomiendo agregar los datos adicionales
                            binding.llNotificacion.visibility = View.VISIBLE
                            binding.btnCrear.isEnabled = false
                            // Llamar a la función con un callback
                            startCountdown(binding.tvContador) { isFinished ->
                                // Este código se ejecuta cuando el contador termina
                                if (isFinished) {
                                    // Aquí puedes manejar lo que pasa cuando el temporizador termine
                                    binding.tvContador.visibility = View.GONE
                                    binding.cbConcentimiento.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                }
                MostrarActionIcon(false)
            }
        }
        // Establecer un listener para cuando el diálogo se cierre
        dialog.setOnDismissListener {
            //MostrarActionIcon(false)
        }
    }

    fun startCountdown(textView: TextView, callback: (Boolean) -> Unit) {
        val countdownTimer = object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Actualizar el TextView con el tiempo restante
                val secondsRemaining = millisUntilFinished / 1000
                textView.text = "Tiempo restante para continuar: $secondsRemaining"
            }

            override fun onFinish() {
                // Cuando termine el contador, mostrar un mensaje final
                //textView.visibility=View.GONE
                callback(true)
            }

        }
        countdownTimer.start()
    }

    fun validarEdittext(editTexts: List<EditText>): Boolean {
        var isValid = true

        for (editText in editTexts) {
            // Verificar si el campo está vacío
            if (editText.text.isNullOrEmpty()) {
                // Mostrar error en el EditText
                editText.error = "Este campo es obligatorio"
                isValid = false
            }
        }
        return isValid
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

    fun objectSend(): Serializable? {
        var file: file? = null
        for (valor in listFile) {
            if (valor.nombre == fileSelected) {
                file = valor
                break
            }
        }
        return file!!
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
    private fun obtenerFecha(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH) + 1)
        val dayOfMonth =
            String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.DAY_OF_MONTH))
        return "$dayOfMonth/$month/$year"
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
}