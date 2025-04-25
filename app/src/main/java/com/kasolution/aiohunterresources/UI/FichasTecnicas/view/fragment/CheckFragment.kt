package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter.ApproveAdapter
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog.ImageViewFragment
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel.ShowModelViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentCheckBinding

class CheckFragment : Fragment() {
    private var _binding: FragmentCheckBinding? = null
    private val binding get() = _binding!!
    private val ShowModelViewModel: ShowModelViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: ApproveAdapter
    private lateinit var lista: ArrayList<VehicleModel>
    private var itemPosition = -1
    private var urlId: urlId? = null
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        lista = ArrayList()
        initUI()
        recuperarPreferencias()
        configSwipe()


        ShowModelViewModel.onCreate(urlId!!, "Publicado", 3, "Revision")
        ShowModelViewModel.showVehicleModel.observe(viewLifecycleOwner, Observer { result ->

            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    adapter.limpiar()
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
        ShowModelViewModel.updateModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let {
                        lista.removeAt(itemPosition)
                        adapter.notifyItemRemoved(itemPosition)
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
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        ShowModelViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else {
                DialogProgress.dismiss()
                if (lista.isEmpty()) {
                    binding.lottieAnimationView.setAnimation(R.raw.no_data_found)
                    binding.lottieAnimationView.playAnimation()
                    binding.llNoData.visibility = View.VISIBLE
                    binding.swipeRefresh.visibility = View.GONE
                } else {
                    binding.llNoData.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                }
            }
        })
        ShowModelViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
        binding.imgBottonBack.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnActualizar.setOnClickListener()
        {
            binding.llNoData.visibility = View.GONE
            ShowModelViewModel.onRefresh(urlId!!, "Publicado", 3, "Revision")
        }

    }

    private fun recuperarPreferencias() {
        val url = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", "")
        val idSheet = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", "")
        urlId = urlId(url!!, "", idSheet!!, "")
    }

    private fun initUI() {
        lmanager = LinearLayoutManager(context)
        adapter = ApproveAdapter(
            listaRecibida = lista,
            OnClickListener = { modelos, action, position ->
                onItemClick(modelos, action, position)
            })
        binding.RvItem.layoutManager = lmanager
        binding.RvItem.adapter = adapter
    }

    private fun onItemClick(modelos: VehicleModel, action: Int, position: Int) {
        itemPosition = position
        if (action == 1) {
            //Modificar Registro
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
                getUserAdmin(),
                "Publicado"
            )
        } else if (action == 2) {
            //eliminar Registro
            ShowModelViewModel.deleteModel(urlId!!, modelos.id)
        } else {
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
    }

    private fun getUserAdmin(): String {
        val name = preferencesUser.getString("NAME", "").toString()
        val lastName = preferencesUser.getString("LASTNAME", "").toString()

        return "$name $lastName"
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
                    ShowModelViewModel.onRefresh(urlId!!, "Publicado", 3, "Revision")
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
}