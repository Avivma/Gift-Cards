package com.example.composefirsttry.preferencescreens

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.composefirsttry.L
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.ActivityPreferenceBinding
import com.example.composefirsttry.preferencescreens.miscellaneous.CategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.ParentCategoryEnum
import com.example.composefirsttry.preferencescreens.miscellaneous.SHARD_PREF_NAME
import com.example.composefirsttry.preferencescreens.widget.TriCheckBox
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.example.composefirsttry.amazon.AmazonIapManager
import kotlinx.coroutines.launch


class PreferenceActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPreferenceBinding
    private lateinit var amazonIapManager: AmazonIapManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.setup()

        sp = application.getSharedPreferences(SHARD_PREF_NAME, Context.MODE_PRIVATE)

        binding = ActivityPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_preference)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)



        val installerPackageName = getInstallerPackageName(this.packageName)
        var purchaseFromAmazonStore = false
        if (installerPackageName!!.startsWith("com.amazon")) {
            // Amazon
            amazonIapManager.init()
            purchaseFromAmazonStore = true
        } else if ("com.android.vending" == installerPackageName) {
            // Google Play
        }

        binding.contentPreferenceLayout.purchaseButton.setOnClickListener { view ->
            if (purchaseFromAmazonStore) {
                this.lifecycleScope.launch {
                    amazonIapManager.purchase("com.amazon.sample.iap.subscription.mymagazine")
                }
            } else {
                // Google Play
            }
        }

        binding.contentPreferenceLayout.sendLogButton.setOnClickListener { view ->
            //send logs:
            L.i("Logs sent (mock)")
        }
        binding.contentPreferenceLayout.sendLogCancelButton.setOnClickListener { view ->
            binding.contentPreferenceLayout.sendLogLayout.visibility = View.GONE
        }

        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            printAllSettings()
        }
    }

    fun getInstallerPackageName(packageName: String): String? {
        kotlin.runCatching {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                this.packageManager.getInstallSourceInfo(packageName).installingPackageName
            else
                this.packageManager.getInstallerPackageName(packageName)
        }
        return null
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_preference)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun getNavController() = this.findNavController(R.id.nav_host_fragment_content_preference)



    private fun printAllSettings() {
        L.i("Print all settings")

        L.i("Parent Categories:")
        val parentCategories = ParentCategoryEnum.values().toCollection(ArrayList())
        parentCategories.forEach { category ->
            val state = sp.getInt(category.text, TriCheckBox.UNCHECKED)
            val stateName = TriCheckBox.getName(state)
            L.i("- ${category.text} : $stateName")
        }


        L.i("Categories:")
        val categories = CategoryEnum.getAll()
        categories.forEach { category ->
            val checked = sp.getBoolean(category.text, false)
            L.i("(${category.index}, ${category.text}) : $checked")
        }
        L.i("")
        L.i("----------------------------------------------------------------------------------------")
        L.i("")
    }

    //(10, candy) : true

    override fun onStart() {
        super.onStart()
        registerReceivers()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceivers()
    }

    private fun registerReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(sendLogReceiver, IntentFilter(SEND_LOGS_ACTION))
    }

    private fun unregisterReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sendLogReceiver)
    }

    private val sendLogReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                SEND_LOGS_ACTION -> {
                    binding.contentPreferenceLayout.sendLogLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object {
        const val LOG_TAG = "TESTING"

        const val SEND_LOGS_ACTION = "send_logs_action"
    }
}