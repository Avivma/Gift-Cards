package com.example.composefirsttry.utils

import androidx.annotation.WorkerThread
import com.example.composefirsttry.L
import com.google.gson.JsonObject
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object MyOkHttpWrapper {
    private enum class RestRequestType {
        GET_REQUEST,
        POST_REQUEST;
        fun getName(): String{
            return when (this) {
                GET_REQUEST -> "Get"
                POST_REQUEST -> "Post"
            }
        }
    }

    private var client: OkHttpClient = OkHttpClient()


    @WorkerThread
    fun getSync (url: String): Response {
        val call = buildCall(url, RestRequestType.GET_REQUEST, null)
        return call.execute()
    }

    @WorkerThread
    suspend fun get(url: String, outerCalledMethodName: String) : ResponseBody {
        return suspendCoroutine<ResponseBody> { continuation ->
            val call = buildCall(url, RestRequestType.GET_REQUEST, null)
            call.enqueue(MyCallback(continuation, outerCalledMethodName))
        }
    }

    @WorkerThread
    suspend fun post(url: String, body: RequestBody, outerCalledMethodName: String) : ResponseBody {
        return suspendCoroutine<ResponseBody> { continuation ->
            val call = buildCall(url, RestRequestType.POST_REQUEST, body)
            call.enqueue(MyCallback(continuation, outerCalledMethodName))
        }
    }

    class MyCallback (private val continuation: Continuation<ResponseBody>, private val outerCalledMethodName: String) : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                continuation.resumeWithException(Exception("Request failed, code = ${response.code()}, message = ${response.message()}, (url = ${call.request().url()})"))
                return
            }
            if (response.body() == null) {
                continuation.resumeWithException(Exception("Request succeeded BUT failed to retrieve correct response, body = null (url = ${call.request().url()})"))
                return
            }

            L.i("$outerCalledMethodName request call succeeded (url = ${call.request().url()})")
            continuation.resume(response.body()!!)
        }
        override fun onFailure(call: Call, e: IOException) {
            continuation.resumeWithException(Exception("Request failed (url = ${call.request().url()})", e))
        }
    }

    fun createBody(jsonData: JsonObject): RequestBody {
        val MEDIA_TYPE = MediaType.parse("application/json")
        return RequestBody.create(MEDIA_TYPE, jsonData.toString())
    }

    private fun buildCall(url: String, requestType: RestRequestType, body: RequestBody?): Call {
        val builder = createBuilder(url)
        L.i("${requestType.getName()} request, url = $url")
        when (requestType) {
            RestRequestType.GET_REQUEST -> builder.get()
            RestRequestType.POST_REQUEST -> builder.post(body!!)
        }
        return createCall(builder)
    }

    private fun createBuilder(url: String): Request.Builder {
        return Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
    }

    private fun createCall(builder: Request.Builder): Call {
        val request = builder.build()
        return client.newCall(request)

    }


    //Callable way:
    fun getWithCallback(url: String, responseCallback: MyOkHttpCallback) {
        val call = buildCall(url, RestRequestType.GET_REQUEST, null)
        call.enqueue(responseCallback)
    }

    fun postWithCallback(url: String, body: RequestBody, responseCallback: MyOkHttpCallback) {
        val call = buildCall(url, RestRequestType.POST_REQUEST, body)
        call.enqueue(responseCallback)
    }

    abstract class MyOkHttpCallback(private val outerCalledMethodName: String) : Callback {
        abstract fun onSuccess(bodyJson: JSONObject)

        abstract fun doesCallSucceeded(bodyJson: JSONObject): Boolean

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val bodyJson = JSONObject(response.body().toString())
                if (doesCallSucceeded(bodyJson)) {
                    onSuccess(bodyJson)
                } else {
                    L.e("$outerCalledMethodName call failed, code = ${response.code()}, message = ${response.message()} 2")
                }
            } else {
                L.e("$outerCalledMethodName call failed, code = ${response.code()}, message = ${response.message()} 1")
            }
        }
        override fun onFailure(call: Call, e: IOException) {
            L.e("$outerCalledMethodName call failed (url = ${call.request().url()})", e)
        }
    }
}