package com.ngxqt.mdm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ngxqt.mdm.R
import com.ngxqt.mdm.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**Cài đặt Bottom Navigation, đặt id của MenuItem trùng với id đích để điều hướng*/
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_home -> {
                    navController.navigate(R.id.homeFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.menu_scan -> {
                    navController.navigate(R.id.scanFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.menu_user -> {
                    navController.navigate(R.id.userFragment)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }
}