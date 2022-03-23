package com.example.camdigikeyclient


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class MainActivity : AppCompatActivity() {
    var loginToken: String? = null
    var loginUrl: String? = null
    private var requestQueue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requireTransparentStatusBar()
        requestQueue = Volley.newRequestQueue(this)
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    findViewById<TextView>(R.id.tvMessage).text =
                        "Approved: ${result.data.toString()}"
                    val authToken = result.data?.getStringExtra("authToken")
                    Log.d("authToken =", "$authToken")
                    val i = Intent(this, ClientInfoActivity::class.java)
                    val flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    flags.let { i.flags = it }
                    i.putExtra("authToken", authToken)
                    startActivity(i)
                } else {
                    findViewById<TextView>(R.id.tvMessage).text =
                        "Rejected: ${result.data.toString()}"
                }
            }

        findViewById<Button>(R.id.btSendRequest).setOnClickListener {
            // MARK : Step 1. Add your logic for requesting `loginToken` from Client API Server
            getLoginToken()
            Log.d("LoginToken", "${loginToken}")
            // MARK : Step 2. Generate login request with `loginToken` to CamDigiKey App
            val intent = Intent(Intent.ACTION_ASSIST).apply {
                data = Uri.parse("camdigikey://login?token=${loginToken}")
                // CamDigiKey Dev Package: kh.gov.camdx.camdigikey.debug
                // CamDigiKey Prod Package: kh.gov.camdx.camdigikey
                setPackage("kh.gov.camdx.camdigikey.debug")
            }

            try {
                resultLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Please install camDigiKey app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requireTransparentStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun getLoginToken() {
        val url = "LOGIN_TOKEN_RESPONDED_FROM_CLIENT_API_SERVER"
        val request = JsonObjectRequest(Request.Method.POST, url, null, { response ->
            try {
                println(response)
                val error: Int = response.getInt("error")
                println("error===== ${error}")
                if (error == 0) {
                    var data = response.getJSONObject("data")
                    this.loginToken = data.getString("loginToken")
                    this.loginUrl = data.getString("loginUrl")
                    println("loginToken = ${loginToken}")
                    println("loginUrl = ${loginUrl}")
                } else {
                    val msg: String = response.getString("message")
                    println("display error ${msg}")
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        requestQueue?.add(request)
    }


}
