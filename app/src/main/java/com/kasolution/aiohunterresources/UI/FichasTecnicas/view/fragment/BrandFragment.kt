package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter.BrandAdapter
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel.BrandViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
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
    private var loaded = false
    private var brandChache: String? = null
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
        if (brandChache != null) {
            val brands = Gson().fromJson(brandChache, Array<Brand>::class.java).toList()
            lista.addAll(brands)
            adapter.notifyDataSetChanged()
        }
        brandViewModel.onCreate(urlId!!)
        brandViewModel.isloading.observe(viewLifecycleOwner, Observer {cargando->
            if (cargando) {
                if (brandChache==null) {
                    DialogProgress.show(requireContext(), "Cargando...")
                }else{
                    binding.ivSearch.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
            } else {
                binding.ivSearch.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                DialogProgress.dismiss()
                if (lista.isEmpty()) {
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
        brandViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
        brandViewModel.BrandModel.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { listaBrand ->
                        lista.clear()
                        lista.addAll(listaBrand)
                        adapter.notifyDataSetChanged()
                        saveBrandCache(listaBrand)
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })
        binding.btnActualizar.setOnClickListener()
        {
            binding.llNoData.visibility = View.GONE
            brandViewModel.onRefresh(urlId!!)
        }
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
                binding.customSearch.requestFocus()
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(binding.customSearch, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        binding.btnAction.setOnClickListener() {
            val checkFragment = CheckFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contenedor, checkFragment)
            fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
            fragmentTransaction.commit()

        }
    }

    private fun initUI() {
        val columnWidthDp = 300
        val columns = resources.displayMetrics.widthPixels / columnWidthDp
        glmanager = GridLayoutManager(context, columns)
        adapter = BrandAdapter(
            listaRecibida = lista,
            OnClickListener = { itemBrand -> onItemSelected(itemBrand) },
            requireContext()
        )
        binding.rvListaBrand.layoutManager = glmanager
        binding.rvListaBrand.adapter = adapter
    }
    private fun saveBrandCache(lista: List<Brand>) {
        val editor = preferencesFichasTecnicas.edit()
        val gson = Gson()
        val json = gson.toJson(lista)
        editor.putString("BRAND_CACHE", json)
        editor.apply()
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
        loaded = true
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
        brandChache = preferencesFichasTecnicas.getString("BRAND_CACHE", null)
        urlId = urlId(url!!, "", idSheet!!, "")
        tipo = preferencesUser.getString("TIPO", "")!!
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
                    brandViewModel.onRefresh(urlId!!)
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