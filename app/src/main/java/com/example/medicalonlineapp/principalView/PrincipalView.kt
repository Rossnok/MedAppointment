package com.example.medicalonlineapp.principalView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.fragment.app.Fragment
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.databinding.ActivityMainBinding
import com.example.medicalonlineapp.databinding.ActivityPrincipalViewBinding
import com.example.medicalonlineapp.fragments.*

class PrincipalView : AppCompatActivity() {

    lateinit var binding : ActivityPrincipalViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_view)
        binding = ActivityPrincipalViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        replaceFragments(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuItemInicio -> replaceFragments(Home())
                R.id.menuItemAgendar -> replaceFragments(Agendar())
                R.id.menuItemCitas -> replaceFragments(Citas())
                R.id.menuItemAnteriores -> replaceFragments(CitasAnteriores())
                R.id.menuItemPerfil -> replaceFragments(profile())
            }

            true
        }

    }

    private fun replaceFragments(fragment: Fragment){

        val fragmentManagger = supportFragmentManager
        val fragmentTransaction = fragmentManagger.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()

    }

}