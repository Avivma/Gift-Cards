package com.example.composefirsttry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.ui.theme.ComposeFirstTryTheme
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type


class MainActivity : ComponentActivity() {
    val gson = GsonBuilder().setPrettyPrinting().create()
//    val gson = GsonBuilder().create()

    val listType: Type = object : TypeToken<ArrayList<JsonObject?>?>() {}.type
    val jsonObjectType: Type = object : TypeToken<JsonObject?>() {}.type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
    }

    private fun setup() {
        L.setup()
    }

    override fun onResume() {
        super.onResume()
        if (true) {
            callGiftCardActivity()
            return
        }
    }

    private fun callGiftCardActivity() {
        val intent = Intent(this, GiftCardMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
        this.finish()
    }

    private fun printJson(jsonTxt: String, log: String) {
        val jsonElement = JsonParser.parseString(jsonTxt)
        val json = gson.toJson(jsonElement)
        L.i("Trying $log:")
        L.i("\n $json")
    }
}