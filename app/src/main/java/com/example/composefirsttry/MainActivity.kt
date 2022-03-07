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
import com.example.composefirsttry.preferencescreens.PreferenceActivity
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
        setContent {
            ComposeFirstTryTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }

    private fun setup() {
        L.setup()
    }

    override fun onResume() {
        super.onResume()

        if (true) {
            callPrefActivity()
            return
        }

        testingJson()

//        testingEnum()
    }

    private fun callPrefActivity() {
        val intent = Intent(this, PreferenceActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
        this.finish()
    }

    private fun testingJson(){
        L.i("Trying jsons:")

        val list: MutableList<String> = mutableListOf()
        list.add("Aviv")
        list.add("Gil")
        list.add("Tom")

        var jsonArray: JsonArray = gson.toJsonTree(list) as JsonArray
        var jsonObj1 = JsonObject()
        jsonObj1.add("names", jsonArray)
        printJson(gson.toJson(jsonObj1), "from list")


        val jsonObjectAviv = JsonObject()
        jsonObjectAviv.addProperty("name", "aviv")
        jsonObjectAviv.addProperty("age", "30")
        jsonObjectAviv.addProperty("work", "yes")
        val jsonAviv: String = gson.toJson(jsonObjectAviv)

        val jsonObjectGil = JsonObject()
        jsonObjectGil.addProperty("name", "Gil")
        jsonObjectGil.addProperty("age", "25")
        jsonObjectGil.addProperty("work", "no")
        val jsonGil: String = gson.toJson(jsonObjectGil)

        val jsonObjectTom = JsonObject()
        jsonObjectTom.addProperty("name", "Tom")
        jsonObjectTom.addProperty("age", "32")
        jsonObjectTom.addProperty("work", "yes")
        val jsonTom: String = gson.toJson(jsonObjectTom)

        list.clear()
        list.add(jsonAviv)
        list.add(jsonGil)
        list.add(jsonTom)

//        val fromJsonAviv: JsonObject = gson.toJsonTree(list[0]) as JsonObject
        val fromJsonAviv: JSONObject = JSONObject(list[0])

        var str = list.joinToString("\n") { it }
        L.i("Trying str:")
        L.i("\n $str")



//        jsonArray = gson.toJsonTree(str) as JsonArray
//        jsonObj1 = JsonObject()
//        jsonObj1.add("persons", jsonArray)
//        printJson(gson.toJson(jsonObj1), "persons - str")


        jsonArray = gson.toJsonTree(list).asJsonArray
        jsonObj1 = JsonObject()
        jsonObj1.add("persons", jsonArray)
        printJson(gson.toJson(jsonObj1), "persons")


        jsonArray = gson.toJsonTree(list).asJsonArray
        jsonObj1 = JsonObject()
        jsonObj1.add("persons", jsonArray)
        val jsonOBJECT = JSONObject(gson.toJson(jsonObj1))
        printJson(jsonOBJECT.toString(2), "persons - jsonOBJECT")


        str = jsonArray.joinToString("\n") { it.asString }
        L.i("Trying str - jsonArray:")
        L.i("\n $str")

//        val arrayOfJsonObject: ArrayList<JsonObject> = gson.fromJson(jsonArray.toString(), listType)
//        jsonArray = gson.toJsonTree(arrayOfJsonObject) as JsonArray
//        jsonObj1 = JsonObject()
//        jsonObj1.add("persons", jsonArray)
//        printJson(gson.toJson(jsonObj1), "persons - arrayOfJsonObject")


//        val jsonArr1: JSONArray = JSONArray(list)
//        val jsonObject: JSONObject = JSONObject()
//        jsonObject.put("details", jsonArr1)
//        printJson(jsonObject.toString(2), "jsonObj")
//        L.i("Trying jsonObj(jsonObj.toString(2)):")
//        L.i("->\n ${jsonObject.toString(2)}")


        var listJsonObject: List<JSONObject> = list.map { JSONObject(it) }
        jsonArray = gson.toJsonTree(listJsonObject) as JsonArray
        jsonObj1 = JsonObject()
        jsonObj1.add("persons", jsonArray)
        printJson(gson.toJson(jsonObj1), "persons - listJsonObject")



        jsonArray = JsonArray()
        list.forEach {
            val jsonObj: JsonObject = gson.fromJson(it, jsonObjectType)
            jsonArray.add(jsonObj)
        }
        jsonObj1 = JsonObject()
        jsonObj1.add("persons", jsonArray)
        printJson(gson.toJson(jsonObj1), "persons - listJsonObject2")


        if (true) return


        val jsonNoArrayTxt: String  = "{\"name\":\"mkyong\",\"age\":35,\"position\":\"Founder\",\"salary\":10000,\"skills\":[\"java\",\"python\",\"shell\"]}"
        val jsonWithArrayTxt = "{\"array\": [ {\"name\":\"Aviv\"}, {\"name\":\"Danny\"}, {\"name\":\"Kobi\"}]}"
        val jsonArrayTxt = "[{\"name\":\"Aviv\"}, {\"name\":\"Danny\"}, {\"name\":\"Kobi\"}]"

        printJson(jsonWithArrayTxt, "jsonWithArray")
        printJson(jsonNoArrayTxt, "jsonNoArray")
        printJson(jsonArrayTxt, "jsonArray")

        val jsonArr: JSONArray = JSONArray(jsonArrayTxt)
        val jsonObj: JSONObject = JSONObject()
        jsonObj.put("details", jsonArr)
        printJson(jsonObj.toString(2), "jsonObj")
        L.i("Trying jsonObj(jsonObj.toString(2)):")
        L.i("->\n ${jsonObj.toString(2)}")
    }

    private fun testingEnum() {
        val ageNameField = TestEnum.AGE.name
        val ageToString = TestEnum.AGE.toString()
        val ageText = TestEnum.AGE.text

        L.i("ageNameField = $ageNameField")
        L.i("ageToString = $ageToString")
        L.i("ageText = $ageText")
    }

    private fun printJson(jsonTxt: String, log: String) {
        val jsonElement = JsonParser.parseString(jsonTxt)
        val json = gson.toJson(jsonElement)
        L.i("Trying $log:")
        L.i("\n $json")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeFirstTryTheme {
        Greeting("Android")
    }
}