package com.kasolution.aiohunterresources.UI.ControlEquipos.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.fragment.EquipmentListFragment
import com.kasolution.aiohunterresources.databinding.ActivityControlEquiposBinding

class ControlEquipos : AppCompatActivity() {
    private lateinit var binding: ActivityControlEquiposBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityControlEquiposBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedorControlEquipos, EquipmentListFragment())
        fragmentTransaction.commit()

    }
}