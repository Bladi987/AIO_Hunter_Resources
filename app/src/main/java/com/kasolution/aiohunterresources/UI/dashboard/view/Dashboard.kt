package com.kasolution.aiohunterresources.UI.dashboard.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.dashboard.view.Fragment.FragmentMenu
import com.kasolution.aiohunterresources.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var binding:ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initComponent()
    }

    private fun initComponent() {
        //iniciamos el menu de opciones
        val bundle = Bundle()
        bundle.putString("key","Menu")
        val fragment = FragmentMenu()
        fragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flcontainerMenu, fragment)
        fragmentTransaction.commit()
    }
}