package com.example.composefirsttry.giftcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.ActivityGiftCardMainBinding
import com.example.composefirsttry.giftcard.repository.GiftCardRepo
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject


class GiftCardMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGiftCardMainBinding

    @Inject
    lateinit var giftCardRepo: GiftCardRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.setup()

        L.i("GiftCardMainActivity - onCreate")

        binding = ActivityGiftCardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController: NavController = getNavController()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.activity_main_bottom_navigation_view)
        setupWithNavController(bottomNavigationView, navController)

//        binding.activityMainBottomNavigationView.setOnNavigationItemSelectedListener {
//        binding.activityMainBottomNavigationView.setOnItemSelectedListener {
//            when(it.itemId){
//                R.id.giftCardsMainFragment -> goToMainFragment()
//                R.id.cardsFragment -> goToCardsFragment()
//            }
//            true
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

//    fun getNavController(): NavController = this.findNavController(R.id.nav_host_fragment)
    fun getNavController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}