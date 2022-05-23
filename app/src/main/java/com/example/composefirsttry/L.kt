package com.example.composefirsttry

import android.util.Log
import androidx.annotation.NonNull
import java.io.IOException
import java.lang.Exception

object L {
    const val TAG = "MyApp"
    var logcatProc: Process? = null
        private set

    fun setup() {
        Thread {
            try {
                logcatProc =
                    Runtime.getRuntime().exec("logcat -v time $TAG:V *:E")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun i(@NonNull message: String?) {
        Log.i(TAG, message!!)
    }

    fun e(message: String?) {
        Log.e(TAG, message!!)
    }

    fun e(@NonNull message: String?, @NonNull e: Throwable?) {
        Log.e(TAG, message, e)
    }
}