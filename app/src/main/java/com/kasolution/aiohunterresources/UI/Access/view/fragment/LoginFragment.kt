package com.kasolution.aiohunterresources.UI.Access.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.kasolution.aiohunterresources.BuildConfig
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Access.viewModel.LoginViewModel
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.UI.User.viewModel.UserViewModel
import com.kasolution.aiohunterresources.UI.dashboard.view.Dashboard
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesControlEquipos: SharedPreferences
    private lateinit var preferencesDocumentos: SharedPreferences
    private lateinit var usuario: user
    private var urlId: urlId? = null
    private var idUser:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesAccess = requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        preferencesCajaChica = requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesFichasTecnicas = requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesControlEquipos = requireContext().getSharedPreferences("valueControlEquipos", Context.MODE_PRIVATE)
        preferencesDocumentos = requireContext().getSharedPreferences("valueDocumentos", Context.MODE_PRIVATE)

        recuperarPreferencias()
        binding.btnIngresar.setOnClickListener() {
            if (binding.etUser.text.toString().isEmpty()) {
                binding.etUser.error = "Campo obligatorio"
            } else if (binding.etPassword.text.toString().isEmpty()) {
                binding.etPassword.error = "Campo obligatorio"
            } else {
                loginViewModel.onCreate(
                    urlId!!,
                    user(
                        "",
                        "",
                        "",
                        binding.etUser.text.toString().trim(),
                        binding.etPassword.text.toString().trim(),
                        "",
                        ""
                    )
                )
            }
        }
        binding.tvVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
        loginViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Espere por favor")
            else DialogProgress.dismiss()
        })

        loginViewModel.Datauser.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val lista = respuesta.getOrNull()
                    lista?.let { User ->
                        usuario = User
                        if(idUser!=null && idUser!=User.id){
                            preferencesCajaChica.edit().clear().apply()
                            preferencesFichasTecnicas.edit().clear().apply()
                            preferencesControlEquipos.edit().clear().apply()
                            preferencesDocumentos.edit().clear().apply()
                        }
                        if (User.id.isNotEmpty() && User.name.isNotEmpty() && User.lastName.isNotEmpty() && User.user.isNotEmpty() && User.password.isNotEmpty() && User.tipo.isNotEmpty())
                            almacenarpref(User.id, User.name, User.lastName, User.tipo)
                        val cambios=procesarKeys(requireContext(),User.keys)
                        if (cambios.isNotEmpty()) {
                            cambios.forEach { Log.i("Preferencias", it) }
                            // Aquí podrías mostrar un diálogo o Snackbar si quieres notificar visualmente.
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
        userViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else {
                DialogProgress.dismiss()
                Toast.makeText(requireContext(), "Contraseña registrada.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        loginViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
        loginViewModel.access.observe(getViewLifecycleOwner(), Observer { valor ->
            if (valor == 1) {
                requireActivity().finish()
                val i = Intent(requireContext(), Dashboard::class.java)
                startActivity(i)

            } else if (valor == 2) {
                resetPassword(
                    "Aviso",
                    "Estimado(a) ${usuario.name} requiere crear una contraseña, por favor ingrese los datos solicitados.",
                    R.drawable.ic_reset
                )
            } else {
                binding.etUser.text?.clear()
                binding.etPassword.text?.clear()
                binding.etUser.requestFocus()
                dialogError("Error!", "Datos ingresados son inconrectos", R.drawable.error_icon)
            }
        })
    }

    private fun procesarKeys(context: Context, key: String): List<String> {
        Log.i("BladiDev","el valor de key es: $key")
        if (key.isNullOrBlank() || key.trim().equals("\"not found\"", ignoreCase = true))
            return emptyList()
        else{
            val keysJson: JsonArray = JsonParser.parseString(key).asJsonArray
            val cambiosRealizados = mutableListOf<String>()

            keysJson.forEach { element ->
                if (element.isJsonObject) {
                    val categoriaJson = element.asJsonObject
                    val nombreCategoria = categoriaJson.keySet().firstOrNull() ?: return@forEach
                    val datos = categoriaJson.getAsJsonObject(nombreCategoria)

                    val prefs = when (nombreCategoria.lowercase()) {
                        "caja chica" -> context.getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
                        "fichas tecnicas" -> context.getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
                        "equipos" -> context.getSharedPreferences("valueControlEquipos", Context.MODE_PRIVATE)
                        "documentos" -> context.getSharedPreferences("valueDocumentos", Context.MODE_PRIVATE)
                        else -> null
                    } ?: return@forEach

                    val editor = prefs.edit()

                    // Verificar si todos los valores son vacíos
                    val todosVacios = datos.entrySet().all { (_, valor) -> valor.asString.isBlank() }

                    if (todosVacios) {
                        // Borrar todos los campos relacionados con esta categoría
                        datos.entrySet().forEach { (clave, _) ->
                            editor.remove(claveMapeado(clave, nombreCategoria))
                        }
                        editor.apply()
                        cambiosRealizados.add("[$nombreCategoria] Todos los valores estaban vacíos, se eliminaron del almacenamiento.")
                        return@forEach
                    }

                    // Si no están vacíos, verificar cambios y actualizar
                    datos.entrySet().forEach { (clave, valor) ->
                        val valorNuevo = valor.asString
                        if (valorNuevo.isNotBlank()) {
                            val clavePrefs = claveMapeado(clave, nombreCategoria)
                            val valorActual = prefs.getString(clavePrefs, null)

                            if (valorNuevo != valorActual) {
                                editor.putString(clavePrefs, valorNuevo)
                                cambiosRealizados.add("[$nombreCategoria] Se actualizó '$clave' de '$valorActual' a '$valorNuevo'")
                            }
                        }
                    }
                    editor.apply()
                }
            }
            return cambiosRealizados
        }
    }


    fun claveMapeado(clave: String, categoria: String): String {
        return when (categoria.lowercase()) {
            "caja chica" -> when (clave) {
                "cajaChica" -> "MONTOCAJACHICA"
                "urlid" -> "URL_SCRIPT"
                "fileid" -> "IDFILE"
                "liquidacionid" -> "IDSHEETLIQUIDACION"
                else -> clave
            }
            "fichas tecnicas" -> when (clave) {
                "urlid" -> "URL_SCRIPT_FICHAS"
                "sheetid" -> "IDSHEET_FICHAS"
                else -> clave
            }
            "equipos" -> when (clave) {
                "urlid" -> "URL_SCRIPT_CONTROL_EQUIPOS"
                "sheetid" -> "IDSHEET_CONTROL_EQUIPOS"
                else -> clave
            }
            "documentos" -> when (clave) {
                "urlid" -> "URL_SCRIPT_DOCUMENTOS"
                "sheetid" -> "IDSHEET_DOCUMENTOS"
                else -> clave
            }
            else -> clave
        }
    }

    private fun recuperarPreferencias() {
        urlId = urlId(
            idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString(),
            "",
            idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString(),
            ""
        )
        idUser=preferencesUser.getString("ID",null)
    }

    private fun almacenarpref(id: String, name: String, lastName: String, tipo: String) {
        val editor = preferencesUser.edit()
        editor.apply {
            putString("ID", id)
            putString("NAME", name)
            putString("LASTNAME", lastName)
            putString("TIPO", tipo)
        }.apply()
    }


    fun dialogError(titulo: String = "", mensaje: String = "", icono: Int = 0) {
        var anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)
        val view = View.inflate(requireContext(), R.layout.dialog_message_error, null)
        val icon = view.findViewById<ImageView>(R.id.iconMessge)
        val tittle = view.findViewById<TextView>(R.id.lbldialogTittle)
        val message = view.findViewById<TextView>(R.id.lblDialogMessage)
        val btnAcept = view.findViewById<Button>(R.id.btndialogacept)
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        view.animation = anim1
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        icon.setImageDrawable(ResourcesCompat.getDrawable(resources, icono, null))
        tittle.text = titulo
        message.text = mensaje

        btnAcept.setOnClickListener() {
            dialog.dismiss()
        }
    }

    fun resetPassword(titulo: String = "", mensaje: String = "", icono: Int = 0) {
        var anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)
        val view = View.inflate(requireContext(), R.layout.dialog_message_reset_password, null)
        val icon = view.findViewById<ImageView>(R.id.iconMessge)
        val tittle = view.findViewById<TextView>(R.id.lbldialogTittle)
        val message = view.findViewById<TextView>(R.id.lblDialogMessage)
        val btnAcept = view.findViewById<Button>(R.id.btndialogGuardar)
        val etpassword = view.findViewById<EditText>(R.id.etPassword)
        val etrepeatpassword = view.findViewById<EditText>(R.id.etRepeatPassword)
        val tverrorShowP = view.findViewById<TextView>(R.id.tvErrorShowP)
        val tverrorShowRP = view.findViewById<TextView>(R.id.tvErrorShowRP)
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()
        view.animation = anim1
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        icon.setImageDrawable(ResourcesCompat.getDrawable(resources, icono, null))
        tittle.text = titulo
        message.text = mensaje
        etpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                var caracteres = etpassword.text.toString()
                tverrorShowP.isVisible = caracteres.length < 8
            }

        })

        etrepeatpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                tverrorShowRP.isVisible =
                    etpassword.text.toString() != etrepeatpassword.text.toString()

            }

        })
        btnAcept.setOnClickListener() {
            if (etpassword.text.toString().isNotEmpty() && etrepeatpassword.text.toString()
                    .isNotEmpty()
            ) {
                if (etpassword.text.toString() == etrepeatpassword.text.toString()) {
                    usuario.password = etpassword.text.toString()
                    userViewModel.updateUser(
                        urlId!!,
                        usuario,
                        action = "login"
                    )
                }
                binding.etPassword.text = etpassword.text
                dialog.dismiss()
            } else {
                etpassword.error = "Campo obligatorio"
                etrepeatpassword.error = "Campo obligatorio"
            }

        }
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