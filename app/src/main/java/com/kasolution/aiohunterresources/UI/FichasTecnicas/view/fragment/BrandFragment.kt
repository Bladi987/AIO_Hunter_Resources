package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Access.AccessActivity
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter.BrandAdapter
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel.BrandViewModel
import com.kasolution.aiohunterresources.UI.User.UserActivity
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentBrandBinding
import java.io.Serializable


class BrandFragment : Fragment() {
    private var _binding: FragmentBrandBinding? = null
    private val binding get() = _binding!!
    private val brandViewModel: BrandViewModel by viewModels()
    private lateinit var glmanager: GridLayoutManager
    private lateinit var adapter: BrandAdapter
    private lateinit var lista: ArrayList<Brand>
    var marcaSeleccionada = ""
    private var tipo = ""
    private var urlId: urlId? = null
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        lista = ArrayList()
        initUI()
        recuperarPreferencias()
        configSwipe()

        //extraerIniciales()

        brandViewModel.onCreate(urlId!!)
        brandViewModel.BrandModel.observe(viewLifecycleOwner, Observer { listaBrand ->
            lista.addAll(listaBrand)
            adapter.notifyDataSetChanged()
        })
        brandViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else DialogProgress.dismiss()
        })

        binding.customSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.btnSearch.setOnClickListener() {
            val animInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_top)
            val animOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_top)
            val animInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
            val animOutRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom)
            if (binding.customSearch.visibility == View.VISIBLE) {
                binding.customSearch.startAnimation(animOutRight)
                binding.ivSearch.setImageResource(R.drawable.ic_search)
                binding.customSearch.text.clear()
                binding.customSearch.visibility = View.GONE
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvTitle.startAnimation(animInLeft)
            } else {
                binding.tvTitle.startAnimation(animOutLeft)
                binding.tvTitle.visibility = View.GONE
                binding.customSearch.startAnimation(animInRight)
                binding.customSearch.visibility = View.VISIBLE
                binding.ivSearch.setImageResource(R.drawable.close_icon)
            }
        }
        binding.btnAction.setOnClickListener() {


            PopupMenuHelper.configureAndShowPopupMenu(
                requireContext(),
                it,
                sortUser(),
                object : PopupMenuHelper.PopupMenuItemClickListener {
                    override fun onMenuItemClicked(item: menuOption) {
                        when (item.texto) {
                            "Usuarios" -> {
                                val i = Intent(context, UserActivity::class.java)
                                startActivity(i)
                            }

                            "Aprobar" -> {
                                val checkFragment = CheckFragment()
                                val fragmentManager = requireActivity().supportFragmentManager
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.contenedor, checkFragment)
                                fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
                                fragmentTransaction.commit()
                            }

                            "Salir" -> {
                                //dialogExit()
                                DialogUtils.dialogQuestion(
                                    requireContext(),
                                    titulo = "Salir",
                                    mensage = "Desea Salir de la apliacion?",
                                    positiveButtontext = "Salir",
                                    onPositiveClick = {
                                        val editor = preferencesFichasTecnicas.edit()
                                        editor.apply {
                                            putString("ID", "")
                                            putString("NAME", "")
                                            putString("LASTNAME", "")
                                            putString("TIPO", "")
                                        }.apply()
                                        requireActivity().finish()
                                        var i = Intent(requireContext(), AccessActivity::class.java)
                                        startActivity(i)
                                    })
                            }
                        }
                    }

                })
        }
        binding.btnback.setOnClickListener() {
            requireActivity().finish()
        }
    }


//    private fun extraerIniciales() {
//        val part = "$name $lastName".split(" ")
//        if (part?.size!! > 1) binding.tvInitialUser.text =
//            part[0][0].toString() + part[1][0].toString()
//        else binding.tvInitialUser.text = part[0][0].toString() + part[0][1].toString()
//    }

    private fun initUI() {
        val columnWidthDp = 300
        val columns = resources.displayMetrics.widthPixels / columnWidthDp
        glmanager = GridLayoutManager(context, columns)
        adapter = BrandAdapter(
            listaRecibida = lista,
            OnClickListener = { itemBrand -> onItemSelected(itemBrand) })
        binding.rvListaBrand.layoutManager = glmanager
        binding.rvListaBrand.adapter = adapter
    }

    private fun onItemSelected(itemBrand: Brand) {
        marcaSeleccionada = itemBrand.brand
        val detailsFragment = BrandDetailsFragment()
        val args = Bundle().apply {
            putSerializable("lista", objectSend())
        }
        detailsFragment.arguments = args

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedor, detailsFragment)
        fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
        fragmentTransaction.commit()
        binding.customSearch.text.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrandBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    fun objectSend(): Serializable? {
        var marca: Brand? = null
        for (valor in lista) {
            if (valor.brand == marcaSeleccionada) {
                marca = valor
                break
            }
        }
        return marca!!
    }

    fun recuperarPreferencias() {
        val url = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", "")
        val idSheet = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", "")
        urlId = urlId(url!!, "", idSheet!!, "")
        tipo = preferencesUser.getString("TIPO", "")!!
    }

    private fun sortUser(): ArrayList<menuOption> {
        val lista: ArrayList<menuOption> = ArrayList()
        when (tipo) {
            "Administrador" -> {
                lista.add(menuOption(R.drawable.user_icon, "Usuarios"))
                lista.add(menuOption(R.drawable.ic_aprobar, "Aprobar"))
                lista.add(menuOption(R.drawable.exit_icon, "Salir"))
            }

            "Coloborador" -> {
                lista.add(menuOption(R.drawable.exit_icon, "Salir"))
            }

            "Invitado" -> {
                lista.add(menuOption(R.drawable.exit_icon, "Salir"))
            }

            "Developer" -> {
                lista.add(menuOption(R.drawable.user_icon, "Usuarios"))
                lista.add(menuOption(R.drawable.ic_aprobar, "Aprobar"))
                lista.add(menuOption(R.drawable.exit_icon, "Salir"))
            }

            else -> {
                lista.add(menuOption(R.drawable.exit_icon, "Salir"))
            }
        }
        return lista
    }
    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            adapter.limpiar()
            brandViewModel.onRefresh(urlId!!)
        }
    }

}