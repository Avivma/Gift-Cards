package com.example.composefirsttry.giftcard

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.ActivityGiftCardMainBinding
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.metadata.repository.MetadataRepo
import com.example.composefirsttry.giftcard.logic.stores.repository.StoresRepo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GiftCardMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGiftCardMainBinding

    @Inject
    lateinit var storesRepo: StoresRepo

    @Inject
    lateinit var sp: SharedPreferences

    @Inject
    lateinit var cardsRepo: CardsRepo

    @Inject
    lateinit var metadataRepo: MetadataRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.setup()

        L.i("GiftCardMainActivity - onCreate")

        binding = ActivityGiftCardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWithNavController(binding.activityMainBottomNavigationView, getNavController())

        if (shouldNavigateToInitializeScreen()) {
            navigateToInitializeScreen()
            return
        }

        if (shouldNavigateToLandingScreen()) {
            navigateToLandingScreen()
            return
        }

/*        binding.activityMainBottomNavigationView.setOnNavigationItemSelectedListener {
        binding.activityMainBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.giftCardsMainFragment -> goToMainFragment()
                R.id.cardsFragment -> goToCardsFragment()
            }
            true
        }*/
    }

    private fun shouldNavigateToInitializeScreen(): Boolean = !metadataRepo.isMetadataExist()
    private fun shouldNavigateToLandingScreen(): Boolean = !cardsRepo.hasAnyCard()

    private fun navigateToInitializeScreen() {
        navigateToScreen(R.id.go_to_initializeFragment)
    }

    private fun navigateToLandingScreen() {
        navigateToScreen(R.id.go_to_landingFragment)
    }

    private fun navigateToScreen(@IdRes resId: Int) {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.giftCardsMainFragment, true)
            .build()
        getNavController().navigate(resId, null, navOptions)
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

    fun displayBottomNavigation(display: Boolean) {
        binding.activityMainBottomNavigationView.visibility = if (display) View.VISIBLE else View.GONE
    }
}