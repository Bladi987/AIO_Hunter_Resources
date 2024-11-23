package com.kasolution.aiohunterresources.UI.Access

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Access.view.fragment.LoginFragment
import com.kasolution.aiohunterresources.databinding.ActivityAccessBinding

class AccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccessBinding
    private lateinit var preferencesAccess: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesAccess = getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flContenedor, LoginFragment())
        fragmentTransaction.commit()

    }

    private fun recuperarPreferenciasUser(): Boolean {
        var id = preferencesAccess.getString("ID", "")
        var name = preferencesAccess.getString("NAME", "")
        var lastName = preferencesAccess.getString("LASTNAME", "")
        var tipo = preferencesAccess.getString("TIPO", "")
        return !(id.isNullOrEmpty() || name.isNullOrEmpty() || lastName.isNullOrEmpty() || tipo.isNullOrEmpty())
    }
}