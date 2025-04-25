package com.kasolution.aiohunterresources.UI.Settings.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Settings.view.model.itemContactUs
import com.kasolution.aiohunterresources.UI.Settings.viewModel.settingsViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentContactBinding
import java.util.Date
import java.text.SimpleDateFormat


class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private val settingsViewModel: settingsViewModel by viewModels()
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    private var urlId: urlId? = null
    private lateinit var userName: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recuperarPreferencias()

        binding.btnSendMessage.setOnClickListener() {
            if (binding.etUser.text.toString().isEmpty()) {
                binding.etUser.error = "Ingrese su nombre"
            }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()) {
                binding.etEmail.error = "Correo electrónico no válido"
            }else if (binding.etMessage.text.toString().isEmpty()) {
                binding.etMessage.error = "Ingrese su mensaje"
            }else{
                val item = itemContactUs(
                    binding.etUser.text.toString(),
                    binding.etEmail.text.toString(),
                    obtenerFecha(),
                    binding.etMessage.text.toString()
                )
                settingsViewModel.insertMessage(urlId!!, item)
            }
        }
        settingsViewModel.isloading.observe(viewLifecycleOwner){
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else DialogProgress.dismiss()
        }

        settingsViewModel.sendMessages.observe(viewLifecycleOwner) { result ->
            result.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let {
                        DialogUtils.dialogMessageResponse(requireContext(),"Mensaje Enviado Exitosamente")
                        binding.etMessage.setText("")
                        binding.etEmail.setText("")
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        }
    }

    private fun recuperarPreferencias() {
        preferencesAccess =
            requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)

        urlId = urlId(
            idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString(),
            "",
            idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString(),
            ""
        )
        binding.etUser.setText(preferencesUser.getString("NAME", "") + " " + preferencesUser.getString("LASTNAME", ""))

    }

    private fun obtenerFecha(): String {
        val fechaHoy: Date = Date()
        val formateador: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy") // Formato deseado
        return formateador.format(fechaHoy)
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