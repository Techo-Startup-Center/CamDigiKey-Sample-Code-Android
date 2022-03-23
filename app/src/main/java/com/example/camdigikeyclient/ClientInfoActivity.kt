package com.example.camdigikeyclient

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class ClientInfoActivity : AppCompatActivity() {

    private var requestQueue: RequestQueue? = null
    var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_info)
        requestQueue = Volley.newRequestQueue(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        val authToken = intent.extras?.getString("authToken")
        if (authToken != null) {
            getAccessToken(authToken)
        }
    }

    private fun getAccessToken(authToken: String) {
        // MARK : Step 3. Add your logic to request for `accessToken` with `authToken` from Client API Server
        val url = "ACCESS_TOKEN_RESPONDED_FROM_CLIENT_API_SERVER"
        val jsonRequest = JSONObject()
        jsonRequest.put("authToken", authToken)
        val request = JsonObjectRequest(Request.Method.POST, url, jsonRequest, { response ->
            try {
                Log.d("getAccessToken", "$response")
                val error: Int = response.getInt("error")
                Log.d("getAccessToken", "error === == ${error}")
                if (error == 0) {
                    var data = response.getJSONObject("data")
                    accessToken = data.get("accessToken").toString()
                    if (accessToken != null) {
                        getUserInfo()
                    }
                } else {
                    val msg: String = response.getString("message")
                    Log.d("getAccessToken", "display error ${msg}")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        requestQueue?.add(request)
    }

    private fun getUserInfo() {
        // MARK : Step 4. Add your logic to request for user information with `accessToken` from Client API Server
        val url = "USER_INFO_RESPONDED_FROM_CLIENT_API_SERVER"
        val headers: MutableMap<String, String> = HashMap()
        headers["Authorization"] = "Bearer ${accessToken}"
        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            null,
            object : Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    try {
                        Log.d("getUserInfo", "$response")
                        val error: Int? = response?.getInt("error")
                        Log.d("getUserInfo", "error === == ${error}")
                        if (error == 0) {
                            var data = response.getJSONObject("data")
                            findViewById<TextView>(R.id.tvNameKh).text = "${data.get("last_name_kh")} ${data.get("first_name_kh")}"
                            findViewById<TextView>(R.id.tvName).text = "${data.get("last_name_en")} ${data.get("first_name_en")}"
                            findViewById<TextView>(R.id.tvGender).text = "${data.get("gender")}"
                            findViewById<TextView>(R.id.tvDoB).text = "${data.get("dob")}"
                            findViewById<TextView>(R.id.tvNationality).text = "${data.get("nationality")}"
                            findViewById<TextView>(R.id.tvEmail).text = "${data.get("email")}"
                            findViewById<TextView>(R.id.tvPhone).text = "${data.get("mobile_phone")}"
                        } else {
                            val msg: String? = response?.getString("message")
                            Log.d("getUserInfo", "display error ${msg}")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer ${accessToken}"
                return headers
            }
        }
        requestQueue?.add(request)
    }
}