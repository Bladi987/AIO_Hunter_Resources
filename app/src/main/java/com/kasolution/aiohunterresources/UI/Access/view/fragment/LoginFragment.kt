package com.kasolution.aiohunterresources.UI.Access.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var usuario: user
    private var urlId: urlId? = null
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences

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
        preferencesAccess =
            requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)

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
                        binding.etUser.text.toString(),
                        binding.etPassword.text.toString(),
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
                        if (User.id.isNotEmpty() && User.name.isNotEmpty() && User.lastName.isNotEmpty() && User.user.isNotEmpty() && User.password.isNotEmpty() && User.tipo.isNotEmpty())
                            almacenarpref(User.id, User.name, User.lastName, User.tipo)
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
                var i = Intent(requireContext(), Dashboard::class.java)
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


    private fun recuperarPreferencias() {
        urlId = urlId(
            idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString(),
            "",
            idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString(),
            ""
        )
//        val id = preferencesAccess.getString("ID", "")
//        val name = preferencesAccess.getString("NAME", "")
//        val lastName = preferencesAccess.getString("LASTNAME", "")
//        val tipo = preferencesAccess.getString("TIPO", "")
//        return !(id.isNullOrEmpty() || name.isNullOrEmpty() || lastName.isNullOrEmpty() || tipo.isNullOrEmpty())
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

    private fun getIds(public: Boolean): String {
        val idScript: String
        val idSheet: String
        if (public) {
            idScript = preferencesAccess.getString("IDSCRIPTPUBLIC", "").toString()
            idSheet = preferencesAccess.getString("IDSHEETPUBLIC", "").toString()
        } else {
            idScript = preferencesAccess.getString("IDSCRIPTPRIVATE", "").toString()
            idSheet = preferencesAccess.getString("IDSHEETPRIVATE", "").toString()
        }
        return "$idScript->$idSheet"
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