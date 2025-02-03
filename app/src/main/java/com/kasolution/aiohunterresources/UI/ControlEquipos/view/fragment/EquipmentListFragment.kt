package com.kasolution.aiohunterresources.UI.ControlEquipos.view.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.RegisterAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.RegisterViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.customSwitch
import com.kasolution.aiohunterresources.databinding.FragmentEquipmentListBinding
import java.util.ArrayList

class EquipmentListFragment : Fragment() {
    private lateinit var binding: FragmentEquipmentListBinding
    private var isMobileSelected = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.customSwitch.setOnSwitchChangedListener(object : customSwitch.OnSwitchChangedListener {
//           override fun onSwitchChanged(isMobileSelected: Boolean) {
//                // Aquí puedes manejar el cambio de estado
//                isMobileSelected = isMobileSelected
//                // Realiza la acción que quieras con el nuevo estado
//                if (isMobileSelected) {
//                    // Acción cuando se selecciona "Montos"
//                    Log.d("Switch", "Se seleccionó Montos")
//                } else {
//                    // Acción cuando se selecciona "Cantidad de equipos"
//                    Log.d("Switch", "Se seleccionó Cantidad de equipos")
//                }
//            }
//        })

    }

}