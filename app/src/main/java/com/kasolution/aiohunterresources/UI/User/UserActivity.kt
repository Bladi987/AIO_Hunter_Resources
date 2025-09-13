package com.kasolution.aiohunterresources.UI.User

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.User.adapter.UserAdapter
import com.kasolution.aiohunterresources.UI.User.fragment.AddUserFragment
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.UI.User.viewModel.UserViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val UserViewModel: UserViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: UserAdapter
    private lateinit var lista: ArrayList<user>
    private var itemPosition = -1
    private var urlId: urlId? = null
    private lateinit var preferencesAccess: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferencesAccess = getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        lista = ArrayList()
        initUI()
        recuperarPreferencias()
        UserViewModel.onCreate(urlId!!)
        UserViewModel.user.observe(this, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val listaUser = respuesta.getOrNull()
                    listaUser?.let { listaUser ->
                        lista.addAll(listaUser)
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
        UserViewModel.isloading.observe(this, Observer {
            if (it) DialogProgress.show(this, "Cargando...")
            else DialogProgress.dismiss()
        })
        UserViewModel.exception.observe(this, Observer { error ->
            showMessageError(error)
        })
        UserViewModel.insertUser.observe(this, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val usuarioData = respuesta.getOrNull()
                    usuarioData?.let { usuario ->
                        lista.add(usuario)
                        adapter.notifyItemInserted(lista.size - 1)
                        lmanager.scrollToPositionWithOffset(lista.size - 1, 10)
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }

        })
        UserViewModel.updateUser.observe(this, Observer { result -> //usuario ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val usuarioData = respuesta.getOrNull()
                    usuarioData?.let { usuario ->
                        lista[itemPosition] = usuario
                        adapter.notifyItemChanged(itemPosition)
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        UserViewModel.deleteUser.observe(this, Observer { id ->
            lista.removeAt(itemPosition)
            adapter.notifyItemRemoved(itemPosition)
        })
        UserViewModel.reset.observe(this, Observer { id ->
            Toast.makeText(this, "Usuario fue Reseteado", Toast.LENGTH_SHORT).show()
        })
        binding.fbAddUser.setOnClickListener() {
            val dialogFragment = AddUserFragment()
            dialogFragment.isCancelable = false
//            dialogFragment.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogFragment.show(supportFragmentManager, "AddUserFrament")
        }
    }

    private fun initUI() {
        lmanager = LinearLayoutManager(this)
        adapter = UserAdapter(
            listaRecibida = lista,
            onClickListener = { usuario, action, position ->
                onItemClicListener(usuario, action, position)
            })
        binding.RvUser.layoutManager = lmanager
        binding.RvUser.adapter = adapter
    }

    private fun recuperarPreferencias() {
        val idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString()
        val idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString()
        urlId = urlId(
            idScript = idScript,
            "",
            idSheet = idSheet,
            ""
        )
    }

    private fun onItemClicListener(usuario: user, action: Int, position: Int) {
        itemPosition = position
        when (action) {
            2 -> {
                //resetear
                DialogUtils.dialogQuestion(
                    this, titulo = "Aviso",
                    mensage = "Desea Resetear el password de ${usuario.name} ${usuario.lastName}?",
                    positiveButtontext = "Aceptar",
                    onPositiveClick = {
                        //quitamos limpiamos el campo password antes de enviar al servidor
                        val userMod=usuario.copy(password = "")
                        //es una accion de reseteo
                        UserViewModel.updateUser(
                            urlId!!,
                            userMod,
                            action = "reset"
                        )
                    })
            }

            3 -> {
                //editar
                val dialogFragment = AddUserFragment()
                dialogFragment.isCancelable = false
                val args = Bundle().apply {
                    putSerializable("usuario", usuario)
                }
                dialogFragment.arguments = args
                dialogFragment.show(supportFragmentManager, "AddUserFrament")
            }

            4 -> {
                //eliminar
                DialogUtils.dialogQuestion(this,
                    titulo = "Aviso",
                    mensage = "Desea Eliminar a ${lista[position].name} ${lista[position].lastName}?",
                    positiveButtontext = "Aceptar",
                    onPositiveClick = {
                        //Se eliminara al usuario
                        itemPosition = position
                        UserViewModel.deleteUser(urlId!!, usuario)
                    })
            }
        }
    }

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            this,
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
        )
    }
}