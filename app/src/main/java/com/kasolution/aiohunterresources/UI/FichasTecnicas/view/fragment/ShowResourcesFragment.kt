package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter.ShowModelAdapter
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog.AddModelFragment
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog.ImageViewFragment
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel.ShowModelViewModel
import com.kasolution.aiohunterresources.core.CustomPicasso
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentShowResourcesBinding
import com.kasolution.recursoshunter.UI.view.Home.Interfaces.DialogListener
import com.squareup.picasso.Callback
import java.io.File
import java.lang.Exception

class ShowResourcesFragment : Fragment(), DialogListener {
    private var _binding: FragmentShowResourcesBinding? = null
    private val binding get() = _binding!!
    private val ShowModelViewModel: ShowModelViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: ShowModelAdapter
    private lateinit var lista: ArrayList<VehicleModel>
    private var modelo: VehicleModel? = null
    private var marca: Brand? = null
    private var itemPosition = -1
    private var tipo = ""
    private var urlId: urlId? = null
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        ClasificarAcceso(tipo)
        arguments?.let {
            modelo = it.getSerializable("model")!! as VehicleModel
            marca = it.getSerializable("brand")!! as Brand
            binding.tvMarca.text = modelo!!.marca
            binding.tvModelo.text = modelo!!.modelo
            cargarIcon(marca!!.icon)
        }
        configSwipe()
        lista = ArrayList()
        recuperarPreferencias()
        initUI()
        ShowModelViewModel.onCreate(urlId!!, binding.tvModelo.text.toString(), 2, "Publicado")
        ShowModelViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else DialogProgress.dismiss()
        })
        ShowModelViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
        ShowModelViewModel.showVehicleModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { listaModel ->
                        lista.addAll(listaModel)
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
        ShowModelViewModel.insertarModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { model ->
                        lista.add(model)
                        adapter.notifyItemInserted(lista.size - 1)
                        lmanager.scrollToPositionWithOffset(lista.size - 1, 10)
                        Toast.makeText(
                            requireContext(),
                            "Registro exitoso, pero estará pendiente de aprobación antes de ser visible.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        ShowModelViewModel.updateModel.observe(viewLifecycleOwner, Observer { result ->
                result?.let { respuesta ->
                    if (respuesta.isSuccess) {
                        val data = respuesta.getOrNull()
                        data?.let { vehiculo ->
                            lista[itemPosition] = vehiculo
                            adapter.notifyItemChanged(itemPosition)
                            Toast.makeText(
                                requireContext(),
                                "Registro actualizado, pero estará pendiente de aprobación antes de ser visible.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val exception = respuesta.exceptionOrNull()
                        exception?.let { ex ->
                            showMessageError(ex.message.toString())
                        }
                    }
                }
            })
        ShowModelViewModel.deleteModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let {
                        lista.removeAt(itemPosition)
                        adapter.notifyItemRemoved(itemPosition)
                        if (lista.isEmpty())
                            requireActivity().supportFragmentManager.popBackStack()
                        else Log.i("BladiDevShowResoucesFragment", "lista aun no esta vacio")
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })

        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.fbAddModel.setOnClickListener() {

            val dialogFragment = AddModelFragment()
            dialogFragment.isCancelable = false
            dialogFragment.listener = this
            dialogFragment.getlistamarcas(marca!!) //desabilitado temporal
            dialogFragment.getListModel(modelo!!)

            dialogFragment.show(childFragmentManager, "AddImageResourceDialogFragment")
        }

    }

    private fun initUI() {
        lmanager = LinearLayoutManager(context)
        adapter = ShowModelAdapter(
            listaRecibida = lista,
            OnClickListener = { modelos, action, position ->
                onItemClick(
                    modelos,
                    action,
                    position
                )
            }, tipo
        )
        binding.RvItem.layoutManager = lmanager
        binding.RvItem.adapter = adapter
    }

    fun recuperarPreferencias() {
        val url = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", "")
        val idSheet = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", "")
        tipo = preferencesUser.getString("TIPO", "")!!
        urlId = urlId(url!!, "", idSheet!!, "")
    }

    private fun onItemClick(modelos: VehicleModel, action: Int, position: Int) {
        itemPosition = position
        when (action) {
            1 -> {
                //mostrar imagen

                val dialogFragment = ImageViewFragment()
                dialogFragment.isCancelable = true
                //dialogFragment.getlistamarcas(marcas!!) //desabilitado temporal
                val args = Bundle().apply {
                    putString("imagelink", modelos.imagen)
                }
                dialogFragment.arguments = args
                dialogFragment.show(childFragmentManager, "AddImageResourceDialogFragment")
            }

            2 -> {
                //editar item

                val dialogFragment = AddModelFragment()
                dialogFragment.isCancelable = false
                dialogFragment.listener = this
                dialogFragment.getlistamarcas(marca!!) //desabilitado temporal
                dialogFragment.getListModel(modelo!!)
                val args = Bundle().apply {
                    putSerializable("modelo", modelos)
                }
                dialogFragment.arguments = args
                dialogFragment.show(childFragmentManager, "AddImageResourceDialogFragment")
            }

            3 -> {
                //observar item
                DialogUtils.dialogQuestion(
                    requireContext(),
                    titulo = "Advertencia",
                    mensage = "Si es observado ningun usuario podrá consultarlo, Desea Observarlo?",
                    positiveButtontext = "Aceptar",
                    onPositiveClick = {
                        ShowModelViewModel.updateModel(
                            urlId!!,
                            modelos.id,
                            "oldData",
                            "no data",
                            modelos.marca,
                            modelos.modelo,
                            modelos.imagen,
                            modelos.comentarios,
                            modelos.basica,
                            modelos.extra,
                            modelos.autor,
                            "",
                            "Observado"
                        )
                    }
                )
            }

            4 -> {
                // eliminar item
                DialogUtils.dialogQuestion(
                    requireContext(),
                    titulo = "Eliminar",
                    mensage = "Esta a punto de Eliminar el registro de ${modelos.marca} ${modelos.modelo}",
                    positiveButtontext = "Aceptar",
                    onPositiveClick = {
                        ShowModelViewModel.deleteModel(urlId!!, modelos.id)
                    })
            }

            else -> Log.i("BladiDev", "opcion no reconocida")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowResourcesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun cargarIcon(icono: String) {
        val cachedImage = loadImageFromCache(requireContext(), "https://drive.google.com/uc?export=view&id=${icono}")
        if (cachedImage != null) {
            binding.imgLogo.setImageBitmap(cachedImage)
        }

//        val customPicasso = CustomPicasso.getInstance(requireContext())
//        customPicasso.load("https://drive.google.com/uc?export=view&id=${icono}").into(
//            binding.imgLogo,
//            object : Callback {
//                override fun onSuccess() {
//                    binding.imgLogo.animate().alpha(1f).setDuration(300)
//                }
//
//                override fun onError(e: Exception?) {
//                }
//            })
    }

    override fun onDataCollected(
        id: String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    ) {
        ShowModelViewModel.insertModel(
            urlId!!,
            data,
            type,
            marca,
            modelo,
            comentarios,
            basico,
            otros,
            tecnico,
            "",
            "Revision"
        )
    }

    override fun onDataCollectedUpdate(
        id: String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        linkImage: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    ) {
        ShowModelViewModel.updateModel(
            urlId!!,
            id,
            data,
            type,
            marca,
            modelo,
            linkImage,
            comentarios,
            basico,
            otros,
            tecnico,
            "",
            "Revision"
        )
    }

    private fun ClasificarAcceso(tipoUsuario: String) {
        when (tipoUsuario) {
            "Administrador" -> {

            }

            "Coloborador" -> {

            }

            "Invitado" -> {
                binding.fbAddModel.isVisible = false
            }

            "Developer" -> {

            }
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
                    adapter.limpiar()
                    ShowModelViewModel.onRefresh(urlId!!, binding.tvModelo.text.toString(), 2, "Publicado")
                })

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
    fun loadImageFromCache(context: Context, imageUrl: String): Bitmap? {
        val fileName = imageUrl.split("/").last()
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)

        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }
}