package com.example.composefirsttry.giftcard

import android.Manifest.permission.GET_ACCOUNTS
import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.composefirsttry.databinding.ActivityGiftCardMainBinding
import com.example.composefirsttry.giftcard.repository.GiftCardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.composefirsttry.*


class GiftCardMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGiftCardMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.setup()

        L.i("GiftCardMainActivity - onCreate")

        binding = ActivityGiftCardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getNavController() = this.findNavController(R.id.nav_host_fragment)

    fun getMyApplication(): MyApplication = application as MyApplication

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return onOptionsItemSelected(item.itemId)|| item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun onOptionsItemSelected(item: Int): Boolean {
        when(item) {
            R.id.refresh -> {
                lifecycleScope.launch {
                    notifyUserForDataRefreshProcess(true)
                    withContext(Dispatchers.IO) {
                        GiftCardRepo.refresh()
                    }
                    notifyUserForDataRefreshProcess(false)
                }
                return true
            }
        }
        return false
    }

    private fun notifyUserForDataRefreshProcess(startProcess: Boolean) {
        val progressBarVisibility = if (startProcess) View.VISIBLE else View.GONE
        binding.progressbar.visibility = progressBarVisibility
        val toastText = if (startProcess) "Refreshing data..." else "Finish refreshing data"
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }
}